import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import bwapi.Position;
import bwapi.Unit;
import bwta.BaseLocation;

public class OverloadManager {
	private static class OverloadInfo {
		Unit overLoad;
		overloadStatus status;
		Position exploreArea;
		Unit followingAttackUnit;
		List<Unit> dropshipUnitList;
		public OverloadInfo() {
			super();
			dropshipUnitList = new ArrayList<Unit>();
		}
		
	}

	public enum overloadStatus {
		wait, /// < 대기
		scout, /// < 정찰
		withAttackUnit, /// < 공격유닛 따라다님
		dropship /// < 드랍십으로 이용
	};

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
	private static BaseLocation baseLocation = InformationManager.Instance()
			.getMainBaseLocation(InformationManager.Instance().selfPlayer);
	// 정찰할 지역 리스트를 넣어둔다.
	private static List<Position> exploreAreaList = new ArrayList<Position>();

	private static Comparator<Position> positionComparator = new Comparator<Position>() {

		@Override
		public int compare(Position o1, Position o2) {
			double distance1 = o1.getDistance(baseLocation.getX(), baseLocation.getY());
			double distance2 = o1.getDistance(baseLocation.getX(), baseLocation.getY());
			if (distance1 < distance2) {
				return -1;
			} else if (distance1 == distance2) {
				return 0;
			} else {
				return 1;
			}
		}
	};

	public void addExploreArea(Position position) {
		for(Position explorePosition : exploreAreaList) {
			if(explorePosition.getX()==position.getX() && explorePosition.getY()==position.getY()) {
				return;
			}
		}
		exploreAreaList.add(position);
		Collections.sort(exploreAreaList, positionComparator);
	}

	public void removeExploreArea(Position position) {
		for(Position explorePosition : exploreAreaList) {
			if(explorePosition.getX()==position.getX() && explorePosition.getY()==position.getY()) {
				exploreAreaList.remove(position);
				Collections.sort(exploreAreaList, positionComparator);
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
					System.out.println("문제가있어요!!!removeOverload");
				}
				allOverloadList.remove(overloadInfo);
				return;
			}
		}
		System.out.println("문제가있엉요!!removeOverload");
	}

	public void rebalance() {
		if (MyBotModule.Broodwar.getFrameCount() % 100 == 0) {
			tmp();
			System.out.println("exploreAreaList : " + exploreAreaList);
		}
		if (MyBotModule.Broodwar.getFrameCount() % 24 != 0) {
			return;
		}
		System.out.println("세팅전");
		System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
		PriorityQueue<OverloadInfo> queue = priorityQueueInit();
		listInit();
		System.out.println("queue1 : " + queue);
		// 1. 뮤탈 따라다닐 2마리
		List<Unit> mutaliskList = StrategyManager.Instance().myMutaliskList;
		if (mutaliskList != null && mutaliskList.size() != 0 && queue.size() != 0) {
			System.out.println("뮤탈따라다니자");
			queue = rebalanceWithAttackUnitOverload(2, mutaliskList.get(0), queue);
		}else {
			System.out.println("뮤탈없는이유 : " + (mutaliskList != null)  + " " + (mutaliskList.size() != 0) + " " + (queue.size() != 0) );
		}
		System.out.println("queue2 : " + queue);
		// 2. 히드라 따라다닐 2마리
		List<Unit> hydraliskList = StrategyManager.Instance().myHydraliskList;
		if (hydraliskList != null && hydraliskList.size() != 0 && queue.size() != 0) {
			System.out.println("히드라따라다니자 : hydraliskList.get(0) (: " + hydraliskList.get(0).getX() + "," + hydraliskList.get(0).getY() +")");
			queue = rebalanceWithAttackUnitOverload(2, hydraliskList.get(0), queue);
		}else {
			System.out.println("히드라없는이유 : " + (hydraliskList != null)  + " " + (hydraliskList.size() != 0) + " " + (queue.size() != 0) );
		}
		System.out.println("queue3 : " + queue);
		// 3. 정찰시키기
		if (exploreAreaList != null && exploreAreaList.size() != 0 && queue.size() != 0) {
			System.out.println("정찰따라다니자");
			for (Position position : exploreAreaList) {
				queue = rebalanceExploreOverload(position, queue);
				if (queue.size() == 0) {
					break;
				}
			}
		}else {
			System.out.println("정찰없는이유 : " + (exploreAreaList != null) + " " + (exploreAreaList.size() != 0) + " " + (queue.size() != 0));
			
		}
		System.out.println("queue4 : " + queue);
		// 4. 남는 오버로드는 대기
		queue = WaitAll(queue);
		System.out.println("queue5 : " + queue);

		// 5. 명령 수행
		for (OverloadInfo overload : allOverloadList) {
			if (overload.status == overloadStatus.withAttackUnit) {
				commandUtil.move(overload.overLoad, overload.followingAttackUnit.getPosition());
			} else if (overload.status == overloadStatus.scout) {
				commandUtil.move(overload.overLoad, overload.exploreArea);
			} else if (overload.status == overloadStatus.wait) {
				commandUtil.move(overload.overLoad, baseLocation.getPosition());
			} else if (overload.status == overloadStatus.dropship) {
				// 아직 미구현
			}
		}
		System.out.println("세팅후");
		System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
	}

	private static PriorityQueue<OverloadInfo> priorityQueueInit() {
		System.out.println("priorityQueueInit");
		System.out.println("allOverloadlist : " + listTostring(allOverloadList));
		System.out.println("waitOverloadList : " + listTostring(waitOverloadList));
		System.out.println("scoutOverloadList : " + listTostring(scoutOverloadList));
		System.out.println("withAttackUnitOverloadList : " + listTostring(withAttackUnitOverloadList));
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				return o1.overLoad.getID()-o2.overLoad.getID();
			}
		});
		queue.addAll(waitOverloadList);
		queue.addAll(scoutOverloadList);
		queue.addAll(withAttackUnitOverloadList);
		System.out.println("queue : " + queue);
		return queue;
	}

	private static PriorityQueue<OverloadInfo> WaitAll(PriorityQueue<OverloadInfo> waitOverloadQueue) {
		while (waitOverloadQueue.isEmpty() == false) {
			OverloadInfo target = waitOverloadQueue.poll();
			target.status = overloadStatus.wait;
			target.exploreArea = baseLocation.getPosition();
			target.followingAttackUnit = null;
			target.dropshipUnitList.clear();
			waitOverloadList.add(target);
		}
		return waitOverloadQueue;
	}

	private static PriorityQueue<OverloadInfo> rebalanceExploreOverload(Position position,
			PriorityQueue<OverloadInfo> waitOverloadQueue) {
		System.out.println("position : " + position + " queue : " + waitOverloadQueue);
		PriorityQueue<OverloadInfo> queue = new PriorityQueue<OverloadInfo>(10, new Comparator<OverloadInfo>() {
			@Override
			public int compare(OverloadInfo o1, OverloadInfo o2) {
				double distance1 = position.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
				double distance2 = position.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
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
		overload.dropshipUnitList.clear();

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
				double distance2 = target.getDistance(o1.overLoad.getX(), o1.overLoad.getY());
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
			overload.dropshipUnitList.clear();

			withAttackUnitOverloadList.add(overload);
			number--;
			if (number == 0) {
				break;
			}
		}
		return queue;
	}

	private void tmp() {
		System.out.println("여기가문제니?");
		for (BaseLocation baseLocation : InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer)) {
			addExploreArea(baseLocation.getPosition());
			addExploreArea(bwta.BWTA.getNearestChokepoint(baseLocation.getPosition()).getCenter());
		}
	}
	
	private static String listTostring(List<OverloadInfo> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("size : " + list.size() + " units : ");
		for(OverloadInfo overloadInfo : list) {
			sb.append(overloadInfo.overLoad.getID() + " ");
		}
		return sb.toString();
	}
}
