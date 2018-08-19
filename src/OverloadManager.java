import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;

import bwapi.Order;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BaseLocation;

public class OverloadManager {

	private static class OverloadInfo {
		Unit overLoad;
		overloadStatus status;
		Position exploreArea;
		Unit followingAttackUnit;

		public OverloadInfo() {
			super();
		}
	}

	public enum overloadStatus {
		wait, /// < 대기
		scout, /// < 정찰
		withAttackUnit, /// < 공격유닛 따라다님
		dropshipWaitingForUnit, /// < 드랍십으로 이용
		dropshipConcentrating, /// < 드랍십으로 이용
		dropshipAttack, /// < 드랍십으로 이용
	};

	private static class WaitingLoadUnitAndOverload {
		Unit unit;
		OverloadInfo overloadInfo;
	}

	private OverloadManager() {

	}

	private static CommandUtil commandUtil = new CommandUtil();

	private static OverloadManager instance = new OverloadManager();

	public static OverloadManager Instance() {
		return instance;
	}

	private static List<OverloadInfo> allOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> waitOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> scoutOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> withAttackUnitOverloadList = new ArrayList<OverloadInfo>();
	private static List<OverloadInfo> dropshipOverloadList = new ArrayList<OverloadInfo>();
	private static List<WaitingLoadUnitAndOverload> waitDropshipUnitList = new ArrayList<WaitingLoadUnitAndOverload>();
	private static BaseLocation baseLocation = InformationManager.Instance()
			.getMainBaseLocation(InformationManager.Instance().selfPlayer);
	// 정찰할 지역 리스트를 넣어둔다.
	private static List<Position> exploreAreaList = new ArrayList<Position>();

	// 따라다닐 유닛 타입과 넘버
	private Map<UnitType, Integer> withAttackUnitMap = new HashMap<UnitType, Integer>();

	private static Comparator<Position> positionComparator=new Comparator<Position>(){

	@Override public int compare(Position o1,Position o2){double distance1=o1.getDistance(baseLocation.getX(),baseLocation.getY());double distance2=o2.getDistance(baseLocation.getX(),baseLocation.getY());if(distance1<distance2){return-1;}else if(distance1==distance2){return 0;}else{return 1;}}};

	public void addDropshipUnit(Unit unit) {
		WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = new WaitingLoadUnitAndOverload();
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			if (unit.canLoad(overloadInfo.overLoad)) {
				unit.load(overloadInfo.overLoad);
				waitingLoadUnitAndOverload.overloadInfo = overloadInfo;
				waitingLoadUnitAndOverload.unit = unit;
				waitDropshipUnitList.add(waitingLoadUnitAndOverload);
				return;
			}
		}
		PriorityQueue<OverloadInfo> overloadInfoQueue = new PriorityQueue<>(10, new Comparator<OverloadInfo>() {

			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = unit.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = unit.getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		overloadInfoQueue.addAll(waitOverloadList);
		overloadInfoQueue.addAll(scoutOverloadList);
		overloadInfoQueue.addAll(withAttackUnitOverloadList);

		while (overloadInfoQueue.isEmpty() == false) {
			OverloadInfo overloadInfo = overloadInfoQueue.poll();
			if (unit.canLoad(overloadInfo.overLoad)) {
				unit.load(overloadInfo.overLoad);
				waitingLoadUnitAndOverload.overloadInfo = overloadInfo;
				waitingLoadUnitAndOverload.unit = unit;
				waitDropshipUnitList.add(waitingLoadUnitAndOverload);

				if (overloadInfo.status == overloadStatus.wait) {
					waitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.scout) {
					scoutOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.withAttackUnit) {
					withAttackUnitOverloadList.remove(overloadInfo);
				}

				overloadInfo.status = overloadStatus.dropshipWaitingForUnit;
				dropshipOverloadList.add(overloadInfo);
				break;
			}
		}
	}

	public boolean canDropshipsGoAttack() {
		if(dropshipOverloadList.size()==0) {
			return false;
		}
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			if (overloadInfo.status != overloadStatus.dropshipConcentrating
					&& overloadInfo.status != overloadStatus.dropshipAttack) {
				return false;
			}

			if (overloadInfo.status == overloadStatus.dropshipConcentrating
					&& overloadInfo.overLoad.getDistance(new Position(63 * 32, 63 * 32)) > 64) {
				return false;
			}
		}
		return true;
	}

	public void goDropships() {
		if (canDropshipsGoAttack() == false) {
			System.out.println("아직 공격 불가입니다.");
			return;
		}
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			overloadInfo.status = overloadStatus.dropshipAttack;
		}
	}

	public void updateWithCombatUnitNum(UnitType unitType, int num) {
		if (withAttackUnitMap.containsKey(unitType)) {
			if (withAttackUnitMap.get(unitType) != num) {
				withAttackUnitMap.put(unitType, num);
				onUpdate();
			}
		} else {
			withAttackUnitMap.put(unitType, num);
			onUpdate();
		}
	}

	public void addExploreArea(Position position) {
		for (Position explorePosition : exploreAreaList) {
			if (explorePosition.getX() == position.getX() && explorePosition.getY() == position.getY()) {
				return;
			}
		}
		exploreAreaList.add(position);
		Collections.sort(exploreAreaList, positionComparator);
		rebalance();
	}

	public void removeExploreArea(Position position) {
		for (Position explorePosition : exploreAreaList) {
			if (explorePosition.getX() == position.getX() && explorePosition.getY() == position.getY()) {
				exploreAreaList.remove(position);
				Collections.sort(exploreAreaList, positionComparator);
				rebalance();
				break;
			}
		}
	}

	public void addOverload(Unit overload) {
		OverloadInfo overloadInfo = new OverloadInfo();
		overloadInfo.overLoad = overload;
		overloadInfo.status = overloadStatus.wait;
		waitOverloadList.add(overloadInfo);
		allOverloadList.add(overloadInfo);
		rebalance();
	}

	public void removeOverload(Unit overload) {
		for (OverloadInfo overloadInfo : allOverloadList) {
			if (overloadInfo.overLoad.getID() == overload.getID()) {
				if (overloadInfo.status == overloadStatus.wait) {
					waitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.withAttackUnit) {
					withAttackUnitOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.scout) {
					scoutOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					dropshipOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipConcentrating) {
					dropshipOverloadList.remove(overloadInfo);
				} else if (overloadInfo.status == overloadStatus.dropshipAttack) {
					dropshipOverloadList.remove(overloadInfo);
				} else {
					 System.out.println("문제가있어요!!!removeOverload");
				}
				allOverloadList.remove(overloadInfo);
				rebalance();
				return;
			}
		}
		 System.out.println("문제가있어요2!!removeOverload");
	}

	public void rebalance() {
		PriorityQueue<OverloadInfo> queue = priorityQueueInit();
		listInit();

		// 1. 뮤탈 따라다니기
		if (withAttackUnitMap.get(UnitType.Zerg_Mutalisk) != null) {
			List<Unit> mutaliskList = StrategyManager.Instance().myMutaliskList;
			if (mutaliskList != null && mutaliskList.size() != 0 && queue.size() != 0) {
				queue = rebalanceWithAttackUnitOverload(withAttackUnitMap.get(UnitType.Zerg_Mutalisk),
						mutaliskList.get(0), queue);
			}
		}

		// 2. 히드라 따라다니기
		if (withAttackUnitMap.get(UnitType.Zerg_Hydralisk) != null) {
			List<Unit> hydraliskList = StrategyManager.Instance().myHydraliskList;
			if (hydraliskList != null && hydraliskList.size() != 0 && queue.size() != 0) {
				queue = rebalanceWithAttackUnitOverload(2, hydraliskList.get(0), queue);
			}
		}

		// 3. 정찰시키기
		if (exploreAreaList != null && exploreAreaList.size() != 0 && queue.size() != 0) {
			for (Position position : exploreAreaList) {
				queue = rebalanceExploreOverload(position, queue);
				if (queue.size() == 0) {
					break;
				}
			}
		}
		// 4. 남는 오버로드는 대기
		queue = WaitAll(queue);
	}

	public void onUpdate() {
		for (int i = 0; i < waitDropshipUnitList.size(); i++) {
			WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = waitDropshipUnitList.get(i);
			if (waitingLoadUnitAndOverload.unit.isLoaded()) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				if (waitingLoadUnitAndOverload.overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					waitingLoadUnitAndOverload.overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
			} else if (waitingLoadUnitAndOverload.unit
					.canLoad(waitingLoadUnitAndOverload.overloadInfo.overLoad) == false) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				if (waitingLoadUnitAndOverload.overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					waitingLoadUnitAndOverload.overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
				addDropshipUnit(waitingLoadUnitAndOverload.unit);
			}
		}

		if (waitDropshipUnitList.size() == 0) {
			for (OverloadInfo overloadInfo : dropshipOverloadList) {
				if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
					overloadInfo.status = overloadStatus.dropshipConcentrating;
				}
			}
		}

		if(MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}

		for (OverloadInfo overloadInfo : allOverloadList) {
			if (overloadInfo.status == overloadStatus.withAttackUnit) {
				commandUtil.move(overloadInfo.overLoad, overloadInfo.followingAttackUnit.getPosition());
			} else if (overloadInfo.status == overloadStatus.scout) {
				commandUtil.move(overloadInfo.overLoad, overloadInfo.exploreArea);
			} else if (overloadInfo.status == overloadStatus.wait) {
				commandUtil.move(overloadInfo.overLoad, baseLocation.getPosition());
			} else if (overloadInfo.status == overloadStatus.dropshipWaitingForUnit) {
				// 아직 미구현 --> 쓸 일 없음
			} else if (overloadInfo.status == overloadStatus.dropshipConcentrating) {
				commandUtil.move(overloadInfo.overLoad, new Position(63 * 32, 63 * 32));
			} else if (overloadInfo.status == overloadStatus.dropshipAttack) {
				if(overloadInfo.overLoad.getLoadedUnits()==null || overloadInfo.overLoad.getLoadedUnits().size()==0) 
				{
					dropshipOverloadList.remove(overloadInfo);
					overloadInfo.status = overloadStatus.wait;
					waitOverloadList.add(overloadInfo);
				}
				
				System.out.println("overloadInfo.overLoad.getHitPoints() : " + overloadInfo.overLoad.getHitPoints());
				if(overloadInfo.overLoad.getHitPoints()<50){
					System.out.println("피 없으니 내리자");
					if(overloadInfo.overLoad.unloadAll(overloadInfo.overLoad.getPosition(), false)) {
						System.out.println("정상적으로 내렸다");
					}else {
						System.out.println("못내림..");
					}
					continue;
				}
				
				TilePosition enemyBasePosition = StrategyManager.Instance().enemyMainBaseLocation.getTilePosition();
				TilePosition enemyBaseGeysersPosition = StrategyManager.Instance().enemyMainBaseLocation.getGeysers().get(0).getTilePosition();
				TilePosition targetPosition = new TilePosition((enemyBasePosition.getX() + enemyBaseGeysersPosition.getX())/2, (enemyBasePosition.getY() + enemyBaseGeysersPosition.getY())/2);
				System.out.println("enemyPosition : (" + enemyBasePosition.getX() + "," + enemyBasePosition.getY() +")");
				System.out.println("enemyBaseGeysersPosition : (" + enemyBaseGeysersPosition.getX() + "," + enemyBaseGeysersPosition.getY() +")");
				System.out.println("targetPosition : (" + targetPosition.getX() + "," + targetPosition.getY() +")");

				if(enemyBaseGeysersPosition.isValid() == false) {
					commandUtil.move(overloadInfo.overLoad, enemyBasePosition.toPosition());
				}else {
					if(overloadInfo.overLoad.getDistance(targetPosition.toPosition())<32) {
						System.out.println("미네랄 위치 다 옴");
						if (overloadInfo.overLoad.canUnload()) {
							if(overloadInfo.overLoad.unloadAll(overloadInfo.overLoad.getPosition(), false)) {
								System.out.println("정상적으로 내렸다");
							}else {
								System.out.println("못내림..");
							}
						}
					}else {
						System.out.println("이동하자!! targetPosition : " + targetPosition.getX() + " + targetPosition.getY()");
								commandUtil.move(overloadInfo.overLoad, targetPosition.toPosition());
					}
				}
				
				
				
				/*
				
				
				if (overloadInfo.overLoad.getTilePosition() != null && bwta.BWTA.getRegion(overloadInfo.overLoad.getTilePosition()) != null && bwta.BWTA.getRegion(overloadInfo.overLoad.getTilePosition()).getPolygon() != null) 
				{
					if (bwta.BWTA.getRegion(overloadInfo.overLoad.getTilePosition()).getPolygon()
							.isInside(StrategyManager.Instance().enemyMainBaseLocation.getPosition())) 
					{
						if (overloadInfo.overLoad.canUnload()) 
						{
//							if(overloadInfo.overLoad.isMoving()) {
//								
//								overloadInfo.overLoad.stop();
//								//commandUtil.move(overloadInfo.overLoad, overloadInfo.overLoad.getPosition());
//							}
							System.out.println("명령1 : " + overloadInfo.overLoad.getOrder());
							System.out.println("command1 : " + overloadInfo.overLoad.getLastCommand());
							if(overloadInfo.overLoad.unloadAll(overloadInfo.overLoad.getPosition(), false)) 
							{
								System.out.println("명령2 : " + overloadInfo.overLoad.getOrder());
								System.out.println("command2 : " + overloadInfo.overLoad.getLastCommand());
								System.out.println("하차!!오버로드삭제!!!");
							}
						}
					} 
					else 
					{
						System.out.println("아직 미도달 이동!");
						
						commandUtil.move(overloadInfo.overLoad, StrategyManager.Instance().enemyMainBaseLocation.getPosition());
					}
					
				}
				*/
			}
		}
	}

	private static PriorityQueue<OverloadInfo> priorityQueueInit() {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				return o1.overLoad.getID() - o2.overLoad.getID();
			}
		});
		queue.addAll(waitOverloadList);
		queue.addAll(scoutOverloadList);
		queue.addAll(withAttackUnitOverloadList);
		return queue;
	}

	private static PriorityQueue<OverloadInfo> WaitAll(PriorityQueue<OverloadInfo> waitOverloadQueue) {
		while (waitOverloadQueue.isEmpty() == false) {
			OverloadInfo target = waitOverloadQueue.poll();
			target.status = overloadStatus.wait;
			target.exploreArea = baseLocation.getPosition();
			target.followingAttackUnit = null;
			waitOverloadList.add(target);
		}
		return waitOverloadQueue;
	}

	private static PriorityQueue<OverloadInfo> rebalanceExploreOverload(Position position,
			PriorityQueue<OverloadInfo> waitOverloadQueue) {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = position.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = position.getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		queue.addAll(waitOverloadQueue);
		OverloadInfo overload = queue.poll();
		overload.status = overloadStatus.scout;
		overload.exploreArea = position;
		overload.followingAttackUnit = null;

		scoutOverloadList.add(overload);

		return queue;
	}

	private static void listInit() {
		waitOverloadList.clear();
		scoutOverloadList.clear();
		withAttackUnitOverloadList.clear();
	}

	private static PriorityQueue<OverloadInfo> rebalanceWithAttackUnitOverload(int number, Unit target,
			PriorityQueue<OverloadInfo> waitOverloadQueue) {
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = target.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = target.getDistance(o2.overLoad.getX(), o2.overLoad.getY());
				if (distance1 < distance2) {
					return -1;
				} else if (distance1 == distance2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		queue.addAll(waitOverloadQueue);
		while (queue.isEmpty() == false) {
			OverloadInfo overload = queue.poll();
			overload.status = overloadStatus.withAttackUnit;
			overload.exploreArea = null;
			overload.followingAttackUnit = target;

			withAttackUnitOverloadList.add(overload);
			number--;
			if (number == 0) {
				break;
			}
		}
		return queue;
	}

	private static String listTostring(List<OverloadInfo> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("size : " + list.size() + " units : ");
		for (OverloadInfo overloadInfo : list) {
			sb.append(overloadInfo.overLoad.getID() + " ");
		}
		return sb.toString();
	}

	private static String listTostring2(List<WaitingLoadUnitAndOverload> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("size : " + list.size() + " units : ");
		for (WaitingLoadUnitAndOverload waitingLoadUnitAndOverload : list) {
			sb.append(waitingLoadUnitAndOverload.overloadInfo.overLoad.getID() + "&"
					+ waitingLoadUnitAndOverload.unit.getID() + " ");
		}
		return sb.toString();
	}
}
