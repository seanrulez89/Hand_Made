
import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;
import bwta.Region;

/// 게임 초반에 일꾼 유닛 중에서 정찰 유닛을 하나 지정하고, 정찰 유닛을 이동시켜 정찰을 수행하는 class<br>
/// 적군의 BaseLocation 위치를 알아내는 것까지만 개발되어있습니다
public class ScoutManager {

	ArrayList <Integer> scoutUnits = new ArrayList <Integer> ();
	
	private Unit currentScoutUnit;
	private int currentScoutStatus;
	private Position currentScoutTargetPosition = Position.None;
	private BaseLocation currentScoutTargetBaseLocation = null;
	
	private Unit firstScoutUnit;
    private int firstScoutUnitStatus;
    private Position firstScoutTargetPosition = Position.None;
    private BaseLocation firstScoutTargetBaseLocation = null;
    
    private Unit secondScoutUnit = null;
    private int secondScoutUnitStatus;	
	private Position secondScoutTargetPosition = Position.None;
    private BaseLocation secondScoutTargetBaseLocation = null;
		
	private Vector<Position> enemyBaseRegionVertices = new Vector<Position>();
	private int currentScoutFreeToVertexIndex = -1;
	
	public static boolean isDangerousBuilding = false;
	
	private CommandUtil commandUtil = new CommandUtil();
	private static ScoutManager instance = new ScoutManager();
	
	/// static singleton 객체를 리턴합니다
	public static ScoutManager Instance() {
		return instance;
	} 
	
	public enum ScoutStatus {
		NoScout,						///< 정찰 유닛을 미지정한 상태
		MovingToAnotherBaseLocation,	///< 적군의 BaseLocation 이 미발견된 상태에서 정찰 유닛을 이동시키고 있는 상태
		MoveAroundEnemyBaseLocation   	///< 적군의 BaseLocation 이 발견된 상태에서 정찰 유닛을 이동시키고 있는 상태
	};
	
	
    public void onStart() {

		for (Unit unit : MyBotModule.Broodwar.self().getUnits())
		{
			if (unit.getType() == UnitType.Zerg_Overlord)
			{
				firstScoutUnit = unit;
				firstScoutUnitStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();
				firstScoutTargetBaseLocation = getClosestBaseLocation();
			    commandUtil.move(firstScoutUnit, firstScoutTargetBaseLocation.getPosition());
				scoutUnits.add(unit.getID());
				//System.out.println(unit.getID());
			}	
		}
    }
	
	

	/// 정찰 유닛을 지정하고, 정찰 상태를 업데이트하고, 정찰 유닛을 이동시킵니다
	public void update()
	{
		// 1초에 4번만 실행합니다
		if (MyBotModule.Broodwar.getFrameCount() % 6 != 0) return;
		
		// scoutUnit 을 지정하고, scoutUnit 의 이동을 컨트롤함.
		assignScoutIfNeeded();
		moveScoutUnit();

		// 참고로, scoutUnit 의 이동에 의해 발견된 정보를 처리하는 것은 InformationManager.update() 에서 수행함
	}

	/// 정찰 유닛을 필요하면 새로 지정합니다
	public void assignScoutIfNeeded() {
		BaseLocation enemyBaseLocation = InformationManager.Instance()
				.getMainBaseLocation(MyBotModule.Broodwar.enemy());

		if (enemyBaseLocation == null && secondScoutUnit == null) {

			for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
				if (unit.getType() == UnitType.Zerg_Overlord && !scoutUnits.contains(unit.getID())) {

					secondScoutUnit = unit;
					scoutUnits.add(unit.getID());

				}

			}

		}

	}

	
	
	
	
	/// 정찰 유닛을 이동시킵니다
	// 상대방 MainBaseLocation 위치를 모르는 상황이면, StartLocation 들에 대해 아군의 MainBaseLocation에서 가까운 것부터 순서대로 정찰
	// 상대방 MainBaseLocation 위치를 아는 상황이면, 해당 BaseLocation 이 있는 Region의 가장자리를 따라 계속 이동함 (정찰 유닛이 죽을때까지) 
	public void moveScoutUnit() {
		BaseLocation enemyBaseLocation = InformationManager.Instance()
				.getMainBaseLocation(InformationManager.Instance().enemyPlayer);
		BaseLocation myBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());

		if (enemyBaseLocation == null) {
			// assign a scout to go scout it
			if (secondScoutUnit != null) {
				if (secondScoutTargetBaseLocation == null) {
					secondScoutTargetBaseLocation = getClosestBaseLocation();
				} else {
					if (MyBotModule.Broodwar.isExplored(secondScoutTargetBaseLocation.getTilePosition())) {
						secondScoutTargetBaseLocation = getClosestBaseLocation();
					} else {
						commandUtil.move(secondScoutUnit, secondScoutTargetBaseLocation.getPosition());
					}
				}
			}
			
			if (firstScoutUnit != null) {
				if (firstScoutTargetBaseLocation == null) {
					firstScoutTargetBaseLocation = getClosestBaseLocation();
				} else {
					if (MyBotModule.Broodwar.isExplored(firstScoutTargetBaseLocation.getTilePosition())) {
						firstScoutTargetBaseLocation = getClosestBaseLocation();
					} else {
						commandUtil.move(firstScoutUnit, firstScoutTargetBaseLocation.getPosition());
					}
				}
			}
			
			

			
			
			/*
			if (MyBotModule.Broodwar.isExplored(firstScoutTargetBaseLocation.getTilePosition())) {
				// assign a scout to go scout it
				firstScoutTargetBaseLocation = getClosestBaseLocation();
				commandUtil.move(firstScoutUnit, firstScoutTargetBaseLocation.getPosition());
				System.out.println("------------------------------");
			}
*/
			
			
			
			
			
			
			
		}
		// if we know where the enemy region is
		else {
			
			
		
			if (isDangerousBuilding == false) {

				for (Unit unit : MyBotModule.Broodwar.enemy().getUnits()) {
					// morphing hydralisk_den
					if (unit.getBuildType().equals(UnitType.Zerg_Hydralisk_Den)
							|| unit.getBuildType().equals(UnitType.Protoss_Photon_Cannon)
							|| unit.getBuildType().equals(UnitType.Protoss_Cybernetics_Core)
							|| unit.getBuildType().equals(UnitType.Zerg_Spore_Colony)
							|| unit.getBuildType().equals(UnitType.Zerg_Spire)) {
						isDangerousBuilding = true;
						break;
					}

					// complete hydralisk_den
					if (unit.getType().equals(UnitType.Zerg_Hydralisk_Den)
							|| unit.getType().equals(UnitType.Protoss_Photon_Cannon)
							|| unit.getType().equals(UnitType.Protoss_Cybernetics_Core)
							|| unit.getType().equals(UnitType.Zerg_Spore_Colony)
							|| unit.getType().equals(UnitType.Zerg_Spire)) {
						isDangerousBuilding = true;
						break;
					}
				}
			}
				

				if (MyBotModule.Broodwar.enemy().getRace().equals(Race.Terran) || isDangerousBuilding) {
					moveScoutUnitToMyBaseLocation();
					return;
				}

				if (firstScoutUnit.isUnderAttack()) {
					moveScoutUnitToMyBaseLocation();
					return;
				}

				if (secondScoutUnit != null && secondScoutUnit.isUnderAttack()) {
					BaseLocation myMainBaseLocation = InformationManager.Instance()
							.getMainBaseLocation(MyBotModule.Broodwar.self());
					commandUtil.move(secondScoutUnit, myMainBaseLocation.getPosition());
					return;
				}

			


			// 적 기지 도는 코드 - 노승호 0810
			
			
			if (isDangerousBuilding == false) {
				// if scout is exist, move scout into enemy region
				if (firstScoutUnit != null) {

					firstScoutTargetBaseLocation = enemyBaseLocation;

					if (MyBotModule.Broodwar.isExplored(firstScoutTargetBaseLocation.getTilePosition()) == false) {

						firstScoutUnitStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();
						firstScoutTargetPosition = firstScoutTargetBaseLocation.getPosition();
						commandUtil.move(firstScoutUnit, firstScoutTargetPosition);

					} else {

						firstScoutUnitStatus = ScoutStatus.MoveAroundEnemyBaseLocation.ordinal();
						firstScoutTargetPosition = getScoutFleePositionFromEnemyRegionVertices(firstScoutUnit);
						commandUtil.move(firstScoutUnit, firstScoutTargetPosition);
						// 적 기지 도는 코드 - 노승호 0810
					}
				}

				// if scout is exist, move scout into enemy region
				if (secondScoutUnit != null) {

					secondScoutTargetBaseLocation = enemyBaseLocation;

					if (MyBotModule.Broodwar.isExplored(secondScoutTargetBaseLocation.getTilePosition()) == false) {

						secondScoutUnitStatus = ScoutStatus.MovingToAnotherBaseLocation.ordinal();
						secondScoutTargetPosition = secondScoutTargetBaseLocation.getPosition();
						commandUtil.move(secondScoutUnit, secondScoutTargetPosition);

					} else {

						secondScoutUnitStatus = ScoutStatus.MoveAroundEnemyBaseLocation.ordinal();
						secondScoutTargetPosition = getScoutFleePositionFromEnemyRegionVertices(secondScoutUnit);
						commandUtil.move(secondScoutUnit, secondScoutTargetPosition);
						// 적 기지 도는 코드 - 노승호 0810
					}
				}

			}
			
			
			
	
			
			
			
			
			
			
			
		}



	}
	
	
	
    private void moveScoutUnitToMyBaseLocation() {
        BaseLocation myFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(MyBotModule.Broodwar.self());
        commandUtil.move(firstScoutUnit, myFirstExpansionLocation.getPoint());
        if (secondScoutUnit != null) {
            BaseLocation myMainBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self());
            commandUtil.move(secondScoutUnit, myMainBaseLocation.getPosition());
        }
    }

    private BaseLocation getClosestBaseLocation() {
        double closestDistance = 1000000000;
        double tempDistance;
        BaseLocation closestBaseLocation = null;
        // 아군 MainBaseLocation 으로부터 가장 가까운 미정찰 BaseLocation 을 새로운 정찰 대상 currentScoutTargetBaseLocation 으로 잡아서 이동
        for (BaseLocation startLocation : BWTA.getStartLocations()) {
            if (MyBotModule.Broodwar.isExplored(startLocation.getTilePosition())) {
                continue;
            }

            if (startLocation == firstScoutTargetBaseLocation) {
                continue;
            }

            if (startLocation == secondScoutTargetBaseLocation) {
                continue;
            }

            tempDistance = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.self()).getGroundDistance(startLocation) + 0.5;
            if (tempDistance > 0 && tempDistance < closestDistance) {
                closestBaseLocation = startLocation;
                closestDistance = tempDistance;
            }
        }

        return closestBaseLocation;
    }
	
	
	
	
	
	
	
	
		
	

	private Position Position(TilePosition desiredPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public Position getScoutFleePositionFromEnemyRegionVertices(Unit currentScoutUnit)
	{
		// calculate enemy region vertices if we haven't yet
		if (enemyBaseRegionVertices.isEmpty()) {
			calculateEnemyRegionVertices();
		}

		if (enemyBaseRegionVertices.isEmpty()) {
			return MyBotModule.Broodwar.self().getStartLocation().toPosition();
		}

		// if this is the first flee, we will not have a previous perimeter index
		if (currentScoutFreeToVertexIndex == -1)
		{
			// so return the closest position in the polygon
			int closestPolygonIndex = getClosestVertexIndex(currentScoutUnit);

			if (closestPolygonIndex == -1)
			{
				return MyBotModule.Broodwar.self().getStartLocation().toPosition();
			}
			else
			{
				// set the current index so we know how to iterate if we are still fleeing later
				currentScoutFreeToVertexIndex = closestPolygonIndex;
				return enemyBaseRegionVertices.get(closestPolygonIndex);
			}
		}
		// if we are still fleeing from the previous frame, get the next location if we are close enough
		else
		{
			double distanceFromCurrentVertex = enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex).getDistance(currentScoutUnit.getPosition());

			// keep going to the next vertex in the perimeter until we get to one we're far enough from to issue another move command
			while (distanceFromCurrentVertex < 128)
			{
				currentScoutFreeToVertexIndex = (currentScoutFreeToVertexIndex + 1) % enemyBaseRegionVertices.size();
				distanceFromCurrentVertex = enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex).getDistance(currentScoutUnit.getPosition());
			}

			return enemyBaseRegionVertices.get(currentScoutFreeToVertexIndex);
		}
	}

	// Enemy MainBaseLocation 이 있는 Region 의 가장자리를  enemyBaseRegionVertices 에 저장한다
	// Region 내 모든 건물을 Eliminate 시키기 위한 지도 탐색 로직 작성시 참고할 수 있다
	public void calculateEnemyRegionVertices()
	{
		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
		if (enemyBaseLocation == null) {
			return;
		}

		Region enemyRegion = enemyBaseLocation.getRegion();
		if (enemyRegion == null) {
			return;
		}
		final Position basePosition = MyBotModule.Broodwar.self().getStartLocation().toPosition();
		final Vector<TilePosition> closestTobase = MapTools.Instance().getClosestTilesTo(basePosition);
		Set<Position> unsortedVertices = new HashSet<Position>();

		// check each tile position
		for (int i = 0; i < closestTobase.size(); ++i)
		{
			final TilePosition tp = closestTobase.get(i);

			if (BWTA.getRegion(tp) != enemyRegion)
			{
				continue;
			}

			// a tile is 'surrounded' if
			// 1) in all 4 directions there's a tile position in the current region
			// 2) in all 4 directions there's a buildable tile
			boolean surrounded = true;
			if (BWTA.getRegion(new TilePosition(tp.getX() + 1, tp.getY())) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX() + 1, tp.getY()))
					|| BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() + 1)) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() + 1))
					|| BWTA.getRegion(new TilePosition(tp.getX() - 1, tp.getY())) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX() - 1, tp.getY()))
					|| BWTA.getRegion(new TilePosition(tp.getX(), tp.getY() - 1)) != enemyRegion || !MyBotModule.Broodwar.isBuildable(new TilePosition(tp.getX(), tp.getY() - 1)))
			{
				surrounded = false;
			}

			// push the tiles that aren't surrounded 
			// Region의 가장자리 타일들만 추가한다
			if (!surrounded && MyBotModule.Broodwar.isBuildable(tp))
			{
				if (Config.DrawScoutInfo)
				{
					int x1 = tp.getX() * 32 + 2;
					int y1 = tp.getY() * 32 + 2;
					int x2 = (tp.getX() + 1) * 32 - 2;
					int y2 = (tp.getY() + 1) * 32 - 2;
					MyBotModule.Broodwar.drawTextMap(x1 + 3, y1 + 2, "" + BWTA.getGroundDistance(tp, basePosition.toTilePosition()));
					MyBotModule.Broodwar.drawBoxMap(x1, y1, x2, y2, Color.Green, false);
				}

				unsortedVertices.add(new Position(tp.toPosition().getX() + 16, tp.toPosition().getY() + 16));
			}
		}

		Vector<Position> sortedVertices = new Vector<Position>();
		Position current = unsortedVertices.iterator().next();
		enemyBaseRegionVertices.add(current);
		unsortedVertices.remove(current);

		// while we still have unsorted vertices left, find the closest one remaining to current
		while (!unsortedVertices.isEmpty())
		{
			double bestDist = 1000000;
			Position bestPos = null;

			for (final Position pos : unsortedVertices)
			{
				double dist = pos.getDistance(current);

				if (dist < bestDist)
				{
					bestDist = dist;
					bestPos = pos;
				}
			}

			current = bestPos;
			sortedVertices.add(bestPos);
			unsortedVertices.remove(bestPos);
		}

		// let's close loops on a threshold, eliminating death grooves
		int distanceThreshold = 100;

		while (true)
		{
			// find the largest index difference whose distance is less than the threshold
			int maxFarthest = 0;
			int maxFarthestStart = 0;
			int maxFarthestEnd = 0;

			// for each starting vertex
			for (int i = 0; i < (int)sortedVertices.size(); ++i)
			{
				int farthest = 0;
				int farthestIndex = 0;

				// only test half way around because we'll find the other one on the way back
				for (int j= 1; j < sortedVertices.size() / 2; ++j)
				{
					int jindex = (i + j) % sortedVertices.size();

					if (sortedVertices.get(i).getDistance(sortedVertices.get(jindex)) < distanceThreshold)
					{
						farthest = j;
						farthestIndex = jindex;
					}
				}

				if (farthest > maxFarthest)
				{
					maxFarthest = farthest;
					maxFarthestStart = i;
					maxFarthestEnd = farthestIndex;
				}
			}

			// stop when we have no long chains within the threshold
			if (maxFarthest < 4)
			{
				break;
			}

			double dist = sortedVertices.get(maxFarthestStart).getDistance(sortedVertices.get(maxFarthestEnd));

			Vector<Position> temp = new Vector<Position>();

			for (int s = maxFarthestEnd; s != maxFarthestStart; s = (s + 1) % sortedVertices.size())
			{
				
				temp.add(sortedVertices.get(s));
			}

			sortedVertices = temp;
		}

		enemyBaseRegionVertices = sortedVertices;
	}

	public int getClosestVertexIndex(Unit unit)
	{
		int closestIndex = -1;
		double closestDistance = 10000000;

		for (int i = 0; i < enemyBaseRegionVertices.size(); ++i)
		{
			double dist = unit.getDistance(enemyBaseRegionVertices.get(i));
			if (dist < closestDistance)
			{
				closestDistance = dist;
				closestIndex = i;
			}
		}

		return closestIndex;
	}
	
	/// 정찰 유닛을 리턴합니다
	public Unit getScoutUnit()
	{
		return firstScoutUnit;
	}

	// 정찰 상태를 리턴합니다
	public int getScoutStatus()
	{
		return firstScoutUnitStatus;
	}

	/// 정찰 유닛의 이동 목표 BaseLocation 을 리턴합니다
	public BaseLocation getScoutTargetBaseLocation()
	{
		return firstScoutTargetBaseLocation;
	}

	/// 적군의 Main Base Location 이 있는 Region 의 경계선에 해당하는 Vertex 들의 목록을 리턴합니다
	public Vector<Position> getEnemyRegionVertices()
	{
		return enemyBaseRegionVertices;
	}
}