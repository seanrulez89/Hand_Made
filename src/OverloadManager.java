import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;

import bwapi.Position;
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
		dropship /// < 드랍십으로 이용
	};
	
	private static class WaitingLoadUnitAndOverload{
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

	private static Comparator<Position> positionComparator = new Comparator<Position>() {

		@Override
		public int compare(Position o1, Position o2) {
			double distance1 = o1.getDistance(baseLocation.getX(), baseLocation.getY());
			double distance2 = o2.getDistance(baseLocation.getX(), baseLocation.getY());
			if (distance1 < distance2) {
				return -1;
			} else if (distance1 == distance2) {
				return 0;
			} else {
				return 1;
			}
		}
	};
	
	public void addDropshipUnit(Unit unit) {
		WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = new WaitingLoadUnitAndOverload();
		for(OverloadInfo overloadInfo : dropshipOverloadList) {
			if(unit.canLoad(overloadInfo.overLoad)) {
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
		
		while(overloadInfoQueue.isEmpty() == false) {
			OverloadInfo overloadInfo = overloadInfoQueue.poll();
			if(unit.canLoad(overloadInfo.overLoad)) {
				unit.load(overloadInfo.overLoad);
				waitingLoadUnitAndOverload.overloadInfo = overloadInfo;
				waitingLoadUnitAndOverload.unit = unit;
				waitDropshipUnitList.add(waitingLoadUnitAndOverload);
				
				if(overloadInfo.status==overloadStatus.wait) {
					waitOverloadList.remove(overloadInfo);
				}else if(overloadInfo.status==overloadStatus.scout) {
					scoutOverloadList.remove(overloadInfo);
				}else if(overloadInfo.status==overloadStatus.withAttackUnit) {
					withAttackUnitOverloadList.remove(overloadInfo);
				}
				
				overloadInfo.status=overloadStatus.dropship;
				dropshipOverloadList.add(overloadInfo);
				break;
			}
		}
	}

	public void unloadDropshipUnits(Unit overload) {
		for (OverloadInfo overloadInfo : dropshipOverloadList) 
		{
			if (overload.getID() == overloadInfo.overLoad.getID()) 			
			{
				if(overload.getTilePosition()!=null && bwta.BWTA.getRegion(overload.getTilePosition())!=null)
				{
					if(bwta.BWTA.getRegion(overload.getTilePosition()).getPolygon().isInside(StrategyManager.Instance().enemyMainBaseLocation.getPosition()))
					{
						if (overload.canUnload()) 
						{
							overload.unloadAll();
							dropshipOverloadList.remove(overloadInfo);
							overloadInfo.status = overloadStatus.wait;
							waitOverloadList.add(overloadInfo);
							break;
						}
					} 
					else
					{
						commandUtil.move(overload, StrategyManager.Instance().enemyMainBaseLocation.getPosition());
					}
					
				}
				
				/*
				if (overload.unloadAll()) {
					dropshipOverloadList.remove(overloadInfo);
					overloadInfo.status = overloadStatus.wait;
					waitOverloadList.add(overloadInfo);
				}
				break;
				*/
			}
		}
	}
	
/*	
	if (selfUnit.getType() == UnitType.Protoss_Shuttle) {
        if (BWTA.getRegion(selfUnit.getTilePosition()) != null) {
           //System.out.println("BWTA.getRegion(selfUnit.getTilePosition()) != null");
           this.setSelfUnitRegion(BWTA.getRegion(selfUnit.getTilePosition()));
        }
     }
	
	public void reaverAttack(Squad squad, SquadState ss) {
	      
	      int enemyCnt = ss.getEneUnitCnt();
	      List<Unit> unitList = squad.getUnits();
	      
	      if (enemyCnt > 0 && ss.getSelfUnitRegion() != null && ss.getTargetUnit() != null) {
	         if (ss.getSelfUnitRegion().getPolygon().isInside(ss.getTargetUnit().getPosition())) {
	            for (Unit unit : unitList) {
	               if (unit.getType() == UnitType.Protoss_Shuttle) 
	               {
	                  if (unit.canUnload()) 
	                  {
	                     unit.unloadAll();
	                  }
	               } else if(!unit.isLoaded()) {
	                  commandUtil.attackMove(unit, squad.getTargetPosition());
	               }
	            }
	         }

	      } else {
	         for (Unit unit : unitList) {
	            if (unit.getType() == UnitType.Protoss_Shuttle) {
	               unit.rightClick(squad.getTargetPosition());
	            }
	         }
	      }
	   }
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean canDropshipsGoAttack() {
		/*
		if(waitOverloadList.size()!=0) {
			//System.out.println("--------------------------------------------------------------- 001");
			return false;
		}
		*/
		for (OverloadInfo overloadInfo : dropshipOverloadList) {
			if(overloadInfo.overLoad.getDistance(new Position(63*32, 63*32)) > 64 ) 
			{
				commandUtil.move(overloadInfo.overLoad, new Position(63*32, 63*32));
				//System.out.println("--------------------------------------------------------------- 002" + dropshipOverloadList.size());
				return false;
			}
		}
		//System.out.println("--------------------------------------------------------------- 003" + dropshipOverloadList.size());
		return true;
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
				} else if (overloadInfo.status == overloadStatus.dropship) {
					dropshipOverloadList.remove(overloadInfo);
				} else {
					//System.out.println("문제가있어요!!!removeOverload");
				}
				allOverloadList.remove(overloadInfo);
				rebalance();
				return;
			}
		}
		//System.out.println("문제가있엉요!!removeOverload");
	}

	public void rebalance() {
		//System.out.println("세팅전");
		//System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		//System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		//System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		//System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
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

		//System.out.println("세팅후");
		//System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		//System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		//System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		//System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
	}

	public void onUpdate() {
		
		/*
		for(WaitingLoadUnitAndOverload waitingLoadUnitAndOverload : waitDropshipUnitList) {
			if(waitingLoadUnitAndOverload.unit.isLoaded()) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
			}else if(waitingLoadUnitAndOverload.unit.canLoad(waitingLoadUnitAndOverload.overloadInfo.overLoad) == false) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				addDropshipUnit(waitingLoadUnitAndOverload.unit);
				commandUtil.move(waitingLoadUnitAndOverload.overloadInfo.overLoad, new Position(63*32, 63*32));
			}
		}
		*/
		
		/*
		Iterator <WaitingLoadUnitAndOverload> lir = waitDropshipUnitList.iterator();		
		while(lir.hasNext())
		{
			WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = lir.next();
			
			if(waitingLoadUnitAndOverload.unit.isLoaded()) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
			}else if(waitingLoadUnitAndOverload.unit.canLoad(waitingLoadUnitAndOverload.overloadInfo.overLoad) == false) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				addDropshipUnit(waitingLoadUnitAndOverload.unit);
				commandUtil.move(waitingLoadUnitAndOverload.overloadInfo.overLoad, new Position(63*32, 63*32));
			}
		}
		*/
		
		for(int i=0; i<waitDropshipUnitList.size(); i++)
		{
			WaitingLoadUnitAndOverload waitingLoadUnitAndOverload = waitDropshipUnitList.get(i);
			
			if(waitingLoadUnitAndOverload.unit.isLoaded()) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
			}else if(waitingLoadUnitAndOverload.unit.canLoad(waitingLoadUnitAndOverload.overloadInfo.overLoad) == false) {
				waitDropshipUnitList.remove(waitingLoadUnitAndOverload);
				addDropshipUnit(waitingLoadUnitAndOverload.unit);
				commandUtil.move(waitingLoadUnitAndOverload.overloadInfo.overLoad, new Position(63*32, 63*32));
			}
			
		}
		
		
		
		
		
		
		for (OverloadInfo overload : allOverloadList) {
			if (overload.status == overloadStatus.withAttackUnit) {
				commandUtil.move(overload.overLoad, overload.followingAttackUnit.getPosition());
			} else if (overload.status == overloadStatus.scout) {
				commandUtil.move(overload.overLoad, overload.exploreArea);
			} else if (overload.status == overloadStatus.wait) {
				commandUtil.move(overload.overLoad, baseLocation.getPosition());
			} else if (overload.status == overloadStatus.dropship) {
				// 아직 미구현 --> 쓸 일 없음
			}
		}
		//System.out.println("waitDropshipUnitList : " + listTostring2(waitDropshipUnitList));
		//System.out.println("dropshipOverloadList : " + listTostring(dropshipOverloadList));
	}

	private static PriorityQueue<OverloadInfo> priorityQueueInit() {
		//System.out.println("priorityQueueInit");
		//System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		//System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		//System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		//System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
		//System.out.println("dropshipOverloadList : " + listTostring(dropshipOverloadList));
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				return o1.overLoad.getID() - o2.overLoad.getID();
			}
		});
		queue.addAll(waitOverloadList);
		queue.addAll(scoutOverloadList);
		queue.addAll(withAttackUnitOverloadList);
		//System.out.println("queue : " + queue);
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
		dropshipOverloadList.clear();
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
			sb.append(waitingLoadUnitAndOverload.overloadInfo.overLoad.getID() + "&" + waitingLoadUnitAndOverload.unit.getID() + " " );
		}
		return sb.toString();
	}
}
