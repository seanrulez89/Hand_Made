/*
KATA BOT
BasicBot
fastMutalBot
ML_Q_Learning_Bot
Bomb_Drop
Squad_Sample
*/


import java.util.*;

import bwapi.*;
import bwta.*;
import bwta.Region;


public class UnitControl_Zergling {

	private Vector<Position> enemyBaseRegionVertices = new Vector<Position>();
	private int currentScoutFreeToVertexIndex = -1;
	
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;

	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;

	BaseLocation myMainBaseLocation = SM.myMainBaseLocation;
	BaseLocation enemyMainBaseLocation = SM.enemyMainBaseLocation;
	

	
	static int balanceIndex = 0;
	static int gatherIndex = UnitControl_COMMON.moveIndex - 1;


	static Position positionAssigned_01 = null;
	static Position positionAssigned_02 = null;
	static BaseLocation scoutZerglingFleePosition = null;
	static Position willBeThere = null;
	static boolean runrunrun = false;
	static boolean goOut = false;
	


	public Unit getNextTargetOf(UnitType myUnitType, Position averagePosition) {
		Unit nextTarget = null;
		
		double targetHP = 10000;
		double tempHP = 0;		

		
		ArrayList <Unit> mustKillFirst = new ArrayList<Unit>();
		ArrayList <Unit> mustKillSecond = new ArrayList<Unit>();
		ArrayList <Unit> groundUnit = new ArrayList<Unit>();	
		 																			
		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(averagePosition, myUnitType.sightRange())) {

			if (enemy.getPlayer() == enemyPlayer) 
			{
				
				if(enemy.isVisible() == false)
				{
					continue;
				}
				
				if(enemy.isFlying() == true || enemy.isLifted()==true)
				{
					continue;
				}
				else
				{
					groundUnit.add(enemy);
				}
				
				if(enemy.isDefenseMatrixed()==true)
				{
					continue;
				}

				if(enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
						|| enemy.getType().equals(UnitType.Zerg_Defiler)
						|| enemy.getType().equals(UnitType.Protoss_High_Templar)
						|| enemy.getType().equals(UnitType.Terran_Missile_Turret)
						|| enemy.getType().equals(UnitType.Zerg_Spore_Colony)
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon)
						|| enemy.getType().equals(UnitType.Terran_Bunker)
						|| enemy.getType().equals(UnitType.Protoss_Reaver))
				{
					mustKillFirst.add(enemy);					
				}
				
				if(enemy.getType().equals(UnitType.Zerg_Zergling)
						|| enemy.getType().equals(UnitType.Zerg_Hydralisk)
						|| enemy.getType().equals(UnitType.Zerg_Ultralisk)
					//	|| enemy.getType().equals(UnitType.Zerg_Mutalisk)
					//	|| enemy.getType().equals(UnitType.Zerg_Guardian)
						|| enemy.getType().equals(UnitType.Zerg_Broodling)
						|| enemy.getType().equals(UnitType.Zerg_Infested_Terran)
						|| enemy.getType().equals(UnitType.Zerg_Sunken_Colony)
						|| enemy.getType().equals(UnitType.Protoss_Zealot)
						|| enemy.getType().equals(UnitType.Protoss_Dragoon)
						|| enemy.getType().equals(UnitType.Protoss_Dark_Templar)
						|| enemy.getType().equals(UnitType.Protoss_Archon)
						|| enemy.getType().equals(UnitType.Protoss_Reaver)
						|| enemy.getType().equals(UnitType.Protoss_Scarab)
					//	|| enemy.getType().equals(UnitType.Protoss_Arbiter)
					//	|| enemy.getType().equals(UnitType.Protoss_Scout)
					//	|| enemy.getType().equals(UnitType.Protoss_Carrier)
					//	|| enemy.getType().equals(UnitType.Protoss_Interceptor)
						|| enemy.getType().equals(UnitType.Protoss_Photon_Cannon)
						|| enemy.getType().equals(UnitType.Terran_Marine)
						|| enemy.getType().equals(UnitType.Terran_Firebat)
						|| enemy.getType().equals(UnitType.Terran_Ghost)
						|| enemy.getType().equals(UnitType.Terran_Bunker)
						|| enemy.getType().equals(UnitType.Terran_Goliath)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Siege_Mode)
						|| enemy.getType().equals(UnitType.Terran_Siege_Tank_Tank_Mode)
						|| enemy.getType().equals(UnitType.Terran_Vulture)
						|| enemy.getType().equals(UnitType.Terran_Vulture_Spider_Mine)
						|| enemy.getType().equals(UnitType.Terran_Wraith)
						|| enemy.getType().equals(UnitType.Terran_Battlecruiser)
						
						
						
						)
				{
					mustKillSecond.add(enemy);	
				}
				
				
				

			}

		}
		
		
		for(Unit enemy : mustKillFirst)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
		for(Unit enemy : mustKillSecond)
		{
			tempHP = enemy.getHitPoints();
			if (targetHP > tempHP) {
				targetHP = tempHP;
				nextTarget = enemy;
			}
		}
		
		if(nextTarget!=null)
		{return nextTarget;}
		
		

		return nextTarget;
	}
	
	public void setPositionAssigned()
	{
		List<BaseLocation> enemyBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer);

		List<BaseLocation> EXPLocations = BWTA.getBaseLocations();

		double minDistance = 1000000000;
		double distanceFromEnemyLocation = 0;
	
		if(SM.enemyMainBaseLocation!=null)
		{
			for (BaseLocation EXPLocation : EXPLocations)
			{
				for (BaseLocation enemyBaseLocation : enemyBaseLocations) 
				{
					distanceFromEnemyLocation = EXPLocation.getDistance(enemyBaseLocation);

					if (minDistance > distanceFromEnemyLocation) 
					{
						if(EXPLocation.getPosition().equals(StrategyManager.Instance().enemyFirstExpansionLocation.getPosition())==false)
						{
							if(EXPLocation.getPosition().equals(StrategyManager.Instance().enemyMainBaseLocation.getPosition())==false)
							{
								minDistance = distanceFromEnemyLocation;
								positionAssigned_01 = EXPLocation.getPosition();
							}
							
							
						}
						
									
					}
				}
			}
		}
		
		
		
		if(SM.enemyMainBaseLocation!=null)
		{
			positionAssigned_02 = UnitControl_COMMON.positionList.get(4);
		}
		else
		{
			positionAssigned_02 = new Position(63*32, 63*32);
		}
		
		if(SM.enemyMainBaseLocation!=null)
		{
			for(BaseLocation startLocation : BWTA.getStartLocations())
			{
				if(startLocation.getPosition().equals(StrategyManager.Instance().enemyMainBaseLocation.getPosition())==false)
				{
					if(startLocation.getPosition().equals(StrategyManager.Instance().myMainBaseLocation.getPosition())==false)
					{
						scoutZerglingFleePosition = startLocation;
					}
				}
			}
		}
		
		
		
		
		
		
	}
	
	
	private Position PredictMovement(Unit target, int frames) {
		Position pos =  new Position(
				target.getPosition().getX() + (int)(frames * target.getVelocityX()),
				target.getPosition().getY() + (int)(frames * target.getVelocityY())
			);
		//ClipToMap(pos);
		return pos;
	}
	

	
	
	public void update() {
		
		balanceIndex = 0;
		
		if(SM.myZerglingList.size() == 0)
		{
			return;
		}
		
		if(SM.isInitialBuildOrderFinished==true && myPlayer.minerals()>350)
		{
			goOut = true;
		}
		
		if (MyBotModule.Broodwar.getFrameCount() <= 24*60*6.0) 
		
		{
			/*if (SM.enemyMainBaseLocation == null || SM.isInitialBuildOrderFinished == false
					|| (SM.isInitialBuildOrderFinished == true && myPlayer.minerals() < 350)) 
			
			{
*/
				/*
				 * for(Unit unit : SM.myZerglingList) {
				 * 
				 * commandUtil.attackMove(unit, SM.myFirstChokePoint.getCenter()); }
				 */

				if (positionAssigned_01 == null || positionAssigned_02 == null
						|| MyBotModule.Broodwar.getFrameCount() % 24 * 60 * 3 == 0) {
					setPositionAssigned();
				}

				a: for (int i = 0; i < SM.myZerglingList.size(); i++) {
					Unit Zergling = SM.myZerglingList.get(i);

					if (i == 0 && Zergling != null && scoutZerglingFleePosition != null
							&& positionAssigned_02 != null) {

						int cnt = 0;
						for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(Zergling.getPosition(),
								Zergling.getType().sightRange())) {
							if (enemy.getPlayer() == enemyPlayer)// && MyBotModule.Broodwar.getFrameCount() % 24 == 0)
							{
								// System.out.println("적군 유인");
								if (Zergling.getDistance(scoutZerglingFleePosition) >= 32) {
									willBeThere = PredictMovement(enemy, 24);
								}

								//commandUtil.move(Zergling, scoutZerglingFleePosition);
								
								commandUtil.move(Zergling, getScoutFleePositionFromEnemyRegionVertices(Zergling));
								runrunrun = true;
								cnt++;
								continue a;
							}
						}

						if (scoutZerglingFleePosition != null && Zergling.getDistance(scoutZerglingFleePosition) < 32) {
							// System.out.println("적 리셋");
							willBeThere = null;
						}

						if (willBeThere != null && Zergling.getDistance(willBeThere) < 32) {
							// System.out.println("적 리셋");
							willBeThere = null;
						}

						if (cnt == 0) {
							runrunrun = false;

						}

						if (runrunrun == false) {
							if (willBeThere != null) {
								// System.out.println("예상적목표향해이동");
								commandUtil.attackMove(Zergling, willBeThere);
								continue;
							} else {
								// System.out.println("정찰위치로이동");
								commandUtil.attackMove(Zergling, positionAssigned_02);
								continue;
							}

						}

					} else {
						
						
						
						//commandUtil.attackMove(Zergling, SM.myFirstChokePoint.getCenter());
						
						
						Unit nextTarget = null;
						nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
						
												
						if (nextTarget != null) 
						{
							commandUtil.attackUnit(Zergling, nextTarget);
							continue;
						}
						
						
						
						
						if (UnitControl_COMMON.defenseSite.isEmpty()==false && Zergling.isAttacking()==false)
						{
							Position toSearch = UnitControl_COMMON.getClosestDefenseSite(Zergling);
							nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, toSearch);
							
							if(nextTarget!=null) 
							{
								commandUtil.attackMove(Zergling, nextTarget.getPosition());
							}
							
											
						}		
						else
						{
							commandUtil.attackMove(Zergling, SM.myFirstChokePoint.getCenter());
						}
						
						
						
						
						
						
						
						
						
					}

				}

				return;
			//}
		}
		
		/*
		if (MyBotModule.Broodwar.getFrameCount() > 24*60*6.0 && MyBotModule.Broodwar.getFrameCount() < 24*60*6.05)
		{
			
			for(Unit Zergling : SM.myZerglingList)
			{
				commandUtil.attackMove(Zergling, SM.enemyMainBaseLocation.getPosition());
			}
			return;
			
			
			if (positionAssigned_01 == null || positionAssigned_02 == null
					|| MyBotModule.Broodwar.getFrameCount() % 24 * 60 * 3 == 0) {
				setPositionAssigned();
			}

			a: for (int i = 0; i < SM.myZerglingList.size(); i++) {
				Unit Zergling = SM.myZerglingList.get(i);

				if (i == 0 && Zergling != null && scoutZerglingFleePosition != null
						&& positionAssigned_02 != null) {

					int cnt = 0;
					for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(Zergling.getPosition(),
							Zergling.getType().sightRange())) {
						if (enemy.getPlayer() == enemyPlayer)// && MyBotModule.Broodwar.getFrameCount() % 24 == 0)
						{
							// System.out.println("적군 유인");
							if (Zergling.getDistance(scoutZerglingFleePosition) >= 32) {
								willBeThere = PredictMovement(enemy, 24);
							}

							//commandUtil.move(Zergling, scoutZerglingFleePosition);
							
							commandUtil.move(Zergling, getScoutFleePositionFromEnemyRegionVertices(Zergling));
							runrunrun = true;
							cnt++;
							continue a;
						}
					}

					if (scoutZerglingFleePosition != null && Zergling.getDistance(scoutZerglingFleePosition) < 32) {
						// System.out.println("적 리셋");
						willBeThere = null;
					}

					if (willBeThere != null && Zergling.getDistance(willBeThere) < 32) {
						// System.out.println("적 리셋");
						willBeThere = null;
					}

					if (cnt == 0) {
						runrunrun = false;

					}

					if (runrunrun == false) {
						if (willBeThere != null) {
							// System.out.println("예상적목표향해이동");
							commandUtil.attackMove(Zergling, willBeThere);
							continue;
						} else {
							// System.out.println("정찰위치로이동");
							commandUtil.attackMove(Zergling, positionAssigned_02);
							continue;
						}

					}

				} else {
					commandUtil.attackMove(Zergling, SM.enemyMainBaseLocation.getPosition());
				}
				

			}
			
			
			return;
			
			
			
		}
		*/
		
		
		
		
		
		
		
			
		if(positionAssigned_01 == null || positionAssigned_02 == null || MyBotModule.Broodwar.getFrameCount() % 24*60*3 == 0)
		{
			setPositionAssigned();			
		}
		
		
		
		
		
		
		//setStartGatherEndPoint();
		
		if(UnitControl_COMMON.moveIndex == UnitControl_COMMON.BASIC_MOVE_INDEX)
		{
			gatherIndex=UnitControl_COMMON.moveIndex - 1;
		}
		
		if(UnitControl_COMMON.enoughGathered(UnitType.Zerg_Zergling, UnitControl_COMMON.movePosition, 6, 0.5) == true && gatherIndex<UnitControl_COMMON.moveIndex )
		{
			gatherIndex++;
			if(gatherIndex>(UnitControl_COMMON.positionList.size()-1))
			{
				gatherIndex=(UnitControl_COMMON.positionList.size()-1);
			}
		}
		
		//System.out.println("Zergling gatherIndex : " + gatherIndex);
		
		

		ArrayList <Position> defenseSite = UnitControl_COMMON.defenseSite;
		Position movePosition = UnitControl_COMMON.movePosition;
		Unit nextTarget = null;
		int i = 0;


		//List <Unit> goUp = MyBotModule.Broodwar.getUnitsInRadius(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE);
		//MyBotModule.Broodwar.drawCircleMap(SM.enemyFirstChokePoint.getCenter(), 3*Config.TILE_SIZE, Color.Orange);

		
		
		a: for(i=0 ; i<SM.myZerglingList.size() ; i++)
		{
			Unit Zergling = SM.myZerglingList.get(i);
			
			
			if(i==0 && Zergling!=null && scoutZerglingFleePosition != null && positionAssigned_02!=null)
			{
				
				
				int cnt = 0;
				for(Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(Zergling.getPosition(), Zergling.getType().sightRange()))
				{
					if(enemy.getPlayer()==enemyPlayer )//&& MyBotModule.Broodwar.getFrameCount() % 24 == 0)
					{
						//System.out.println("적군 유인");
						if(Zergling.getDistance(scoutZerglingFleePosition) >= 32 ) {
							willBeThere = PredictMovement(enemy, 24);
						}
						
						//commandUtil.move(Zergling, scoutZerglingFleePosition);
						commandUtil.move(Zergling, getScoutFleePositionFromEnemyRegionVertices(Zergling));
						runrunrun = true;
						cnt++;
						continue a;
					}
				}
				
				if(scoutZerglingFleePosition!=null && Zergling.getDistance(scoutZerglingFleePosition) < 32 )
				{
					//System.out.println("적 리셋");
					willBeThere = null;
				}
				
				
				
				
				if(willBeThere!=null && Zergling.getDistance(willBeThere) < 32 )
				{
					//System.out.println("적 리셋");
					willBeThere = null;
				}
				
				
				
				if(cnt==0)
				{
					runrunrun=false;
					
				}
				
				
				if(runrunrun==false)
				{
					if(willBeThere!=null)
					{
						//System.out.println("예상적목표향해이동");
						commandUtil.attackMove(Zergling, willBeThere);
						continue;
					}
					else
					{
						//System.out.println("정찰위치로이동");
						commandUtil.attackMove(Zergling, positionAssigned_02);
						continue;
					}
					
					
				}
				
				
			}
			
			
			
			
			
			/*boolean shouldGoUp = false;
			for(Unit tempUnit : goUp)
			{
				if(Zergling.getID() == tempUnit.getID())
				{
					//System.out.println("길막이라 올라갑니다.");
					commandUtil.move(Zergling, enemyMainBaseLocation.getPosition());
					shouldGoUp = true;
					break;
				}
			}
			
			if(shouldGoUp==true)
			{
				continue;
			}*/
			/*
			if(Zergling.isIrradiated())
			{
				if(Zergling !=null)
				{
					commandUtil.move(Zergling, enemyMainBaseLocation.getPosition());
					continue;
				}
				
			}
			
			if(Zergling.isUnderStorm())
			{
				if(Zergling != null)
				{
					commandUtil.move(Zergling, myMainBaseLocation.getPosition());
					continue;
				}
				
			}
			*/
			nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
			
			/*
			if(i % 10 == 0)
			{
				nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());			
			}
			*/
			
			if (nextTarget != null) 
			{
				commandUtil.attackUnit(Zergling, nextTarget);
				continue;
			}
			
			
			
			
			if (defenseSite.isEmpty()==false && Zergling.isAttacking()==false)
			{
				Position toSearch = UnitControl_COMMON.getClosestDefenseSite(Zergling);
				nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, toSearch);
				
				if(nextTarget!=null) 
				{
					commandUtil.attackMove(Zergling, nextTarget.getPosition());
				}
				
								
			}
			else if(SM.combatState == StrategyManager.CombatState.attackStarted)
			{
				commandUtil.attackMove(Zergling, movePosition);				
			}
			else if (SM.myZerglingList.size() > 12) 
			{			
				if(Zergling!=null)
				{
					balanceIndex = UnitControl_COMMON.defenseBalance(Zergling, balanceIndex);
				}
			}		
			else
			{
				commandUtil.attackMove(Zergling, movePosition);
			}
		
		
		}
		
		
		
		
		
		
		
		
		
		
		/*
		
		if(SM.myZerglingList.size() == 0)
		{
			return;
		}
		
		setStartGatherEndPoint();

		
		boolean endGame = enoughGathered(UnitType.Zerg_Zergling, gatherPoint, 3, 0.3);
		Position invader = null;
		Unit nextTarget = null;
		
		int i = 0;


		//System.out.println("SM.myZerglingList.size() : " + SM.myZerglingList.size());
		
		for(i=0 ; i<SM.myZerglingList.size() ; i++)
		{
			
			Unit Zergling = SM.myZerglingList.get(i);
			
			if(i % (SM.myZerglingList.size()/6+1) == 0)
			{
				invader = UnitControl_COMMON.defenseSite;			
				
				if (invader != null && Zergling.isAttacking()==false)
				{	
					nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, invader);
				}
				else
				{
					nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
				}
				
			}
			else
			{
				if(nextTarget!=null && Zergling.getDistance(nextTarget.getPosition()) > UnitType.Zerg_Zergling.sightRange())
				{
					invader = UnitControl_COMMON.defenseSite;			
					
					if (invader != null && Zergling.isAttacking()==false)
					{	
						nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, invader);
					}
					else
					{
						nextTarget = getNextTargetOf(UnitType.Zerg_Zergling, Zergling.getPosition());
					}
				}
			}
			
			if(Zergling.isIrradiated())
			{
				Zergling.move(SM.enemyMainBaseLocation.getPosition());
				continue;
			}
			
			if(Zergling.isUnderStorm())
			{
				Zergling.move(SM.myMainBaseLocation.getPosition());
				continue;
			}
			
	
			
			
			
			
			if (nextTarget != null) 
			{
				//System.out.println(1);
				commandUtil.attackUnit(Zergling, nextTarget);
				//Zergling.attack(nextTarget);
				continue; // 컨티뉴를 하면 무조건 이 타겟을 치고 안하면 근처를 친다 왜냐하면 어택땅이 기본 상태라서 근접유닛은 안하는게 낫겠다 자꾸 병목현상되니까
			}
			
			
			if(SM.enemyMainBaseLocation == null)
			{
				//System.out.println(3);
				commandUtil.attackMove(Zergling, startPoint);
			}
			else if(CombatState == StrategyManager.CombatState.attackStarted)// 공격나가는 시점		
				{

				if (endGame == false) 
				{
					//System.out.println(7);
					commandUtil.attackMove(Zergling, gatherPoint);
					moveToEndPoint = false;
				}
				else 
				{
					//System.out.println(6);
					commandUtil.attackMove(Zergling, endPoint);
				}

			}
			else if (SM.myZerglingList.size() >12) 
			{
				//System.out.println(9);
				if(Zergling!=null)
				{
					defenseBalance(Zergling);					
				}
			}
			else
			{	
				//System.out.println(10);
				commandUtil.attackMove(Zergling, startPoint);
			}
		}*/
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
	{/*
		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(MyBotModule.Broodwar.enemy());
		if (enemyBaseLocation == null) {
			return;
		}
*/
		if(scoutZerglingFleePosition==null)
		{
			return;
		}
		
		
		Region enemyRegion = scoutZerglingFleePosition.getRegion();
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
	
	
	private static UnitControl_Zergling instance = new UnitControl_Zergling();

	// static singleton 객체를 리턴합니다
	public static UnitControl_Zergling Instance() {
		return instance;
	}
	
	
}
