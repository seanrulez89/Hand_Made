import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class Mutalisk {
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	
	Player myPlayer = SM.myPlayer;
	Race myRace = SM.myRace;

	Player enemyPlayer = SM.enemyPlayer;
	Race enemyRace = SM.enemyRace;

	BaseLocation myMainBaseLocation = SM.myMainBaseLocation;
	BaseLocation enemyMainBaseLocation = SM.enemyMainBaseLocation;



	StrategyManager.CombatState CombatStateNow = SM.combatState;
	ArrayList<Unit> mutalisks = SM.myCombatUnitType2List;

	boolean Mutal_flag;
	public static boolean moveToEndPoint = false;

	public Unit getNextTargetOf(Unit myUnit) {
		Unit nextTarget = null;

		enemyPlayer = SM.enemyPlayer;

		double targetHP = 10000;
		double tempHP = 0;

		for (Unit enemy : MyBotModule.Broodwar.getUnitsInRadius(myUnit.getPosition(),
				myUnit.getType().seekRange() * 10)) { // 저글링 같은 근접공격유닛은 10배 해봐야 무의미한가?

			if (enemy.getPlayer() == enemyPlayer) {
				if (myUnit.canAttack(enemy)) {
					if (enemy.canAttack(myUnit)) // 적군이면서 서로 공격가능하다면 가장 우선순위로 싸우고
					{
						tempHP = enemy.getHitPoints();
						if (targetHP > tempHP) {
							targetHP = tempHP;
							nextTarget = enemy;
						}
					} else if (enemy.canAttack()) // 나만 일방적으로 칠 수 있다면 2순위로 싸우고
					{
						tempHP = enemy.getHitPoints();
						if (targetHP > tempHP) {
							targetHP = tempHP;
							nextTarget = enemy;
						}
					} else // 마지막 3순위 이것은 아마도 건물일 것이다. 나는 공격할 수 있지만, 적군은 아무것도 공격이 안되는 유닛이므로
					{
						tempHP = enemy.getHitPoints();
						if (targetHP > tempHP) {
							targetHP = tempHP;
							nextTarget = enemy;
						}

					}

				}

			}

		}

		return nextTarget;
	}
	
	
	public Position weAreUnderAttack(Unit myUnit) {
		Position underAttackPosition = null;

		double positionDistance = 100000000;
		double tempDistance = 0;

		for (Unit unit : MyBotModule.Broodwar.self().getUnits()) {
			if ((unit.getType().isBuilding() && unit.isUnderAttack())
					|| (unit.getType() == UnitType.Zerg_Drone && unit.isUnderAttack())) {

				tempDistance = unit.getPosition().getDistance(myUnit.getPosition());
				if (positionDistance > tempDistance) {
					positionDistance = tempDistance;
					underAttackPosition = unit.getPosition();

				}

			}
		}

		if(underAttackPosition!=null)
		{
			moveToEndPoint = false;
			
			
			System.out.println("time to defense");
			
			
		}

		
		
		
		return underAttackPosition;
	}
	
	

	public boolean enoughGathered(Unit myUnit, Position targetPosition, double input_ratio) {
		boolean gathered = false;
		double ratio = input_ratio;
		int numberOfGathered = 0;
		int numberOfTotal = 0;

		if(targetPosition == null)
		{
			System.out.println("아직 기지 못 찾음");
			return false;
		}
		
		
		if (myUnit.getType() == SM.myCombatUnitType1) {
			numberOfTotal = SM.myCombatUnitType1List.size();

		} else if (myUnit.getType() == SM.myCombatUnitType2) {
			numberOfTotal = SM.myCombatUnitType2List.size();
		} else {

		}

		List<Unit> unitsAround = MyBotModule.Broodwar.getUnitsInRadius(targetPosition, 3 * Config.TILE_SIZE);

		for (Unit unit : unitsAround) {
			
			if(unit.exists() && unit != null) {
				
				
			
			if (unit.getPlayer() == myPlayer) {
				if (unit.getType() == myUnit.getType()) {
					numberOfGathered++;
				}
			}
			}
		}

		
		
		if(moveToEndPoint==true)
		{
			gathered = true;
			return gathered;
		}
		
		
		
		
		
		if (numberOfGathered > 5  && numberOfGathered > numberOfTotal * ratio) {

			gathered = true; // 0627 이것도 의미를 뒤집던가
			moveToEndPoint = true;
			
			System.out.println("numberOfGathered" + numberOfGathered);
			System.out.println("numberOfTotal" + numberOfTotal);
		}



		return gathered;
	}
	
	
	
	
	
	
	
	public Position getNextPlaceToGo(Unit myUnit, StrategyManager.CombatState CombatState) // 0627 공격이면 포지션 받는 유형으로 함수
																							// 하나 더 짜면 되고, 컴뱃상태도 쓸모가 없네?
	{
		Position underAttack = weAreUnderAttack(myUnit);
		Position nextPlace = bwta.BWTA.getNearestChokepoint(SM.mySecondChokePoint.getCenter()).getCenter();
		Position gatherPoint = null;
		Position endPoint = null;

		// if (enemyMainBaseLocation != null && CombatState ==
		// StrategyManager.CombatState.defenseMode && underAttack == null) // 적위치를 알고
		// 방어모드인데 평화상태라면
		if (enemyMainBaseLocation != null) {
			int x, y;

			x = enemyMainBaseLocation.getX();
			y = enemyMainBaseLocation.getY();

			if (x < 63 * 32) {
				x = 0;
			} else {
				x = 127 * 32;
			}

			if (y < 63 * 32) {
				y = 0;
			} else {
				y = 127 * 32;
			}

			endPoint = new Position(x, y);

			int a = 63 * 32;
			int b = 63 * 32;

			a = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
			b = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

			if (50 * 32 < a && a < 70 * 32) {

				if (enemyMainBaseLocation.getX() < myMainBaseLocation.getX()) {
					a = myMainBaseLocation.getX() / 2;
				} else {
					a = enemyMainBaseLocation.getX() / 2;
				}

			}

			if (a / 32 == 59) // 대각이거나 가로로 나란하거나
			{
				if (enemyMainBaseLocation.getX() == myMainBaseLocation.getX()) {
					;
				} else {
					b = enemyMainBaseLocation.getY();
				}

			} else // 세로로 나란히
			{
				;
			}


			gatherPoint = new Position(a, b);

//			System.out.println("A : " + a / 32);
//			System.out.println("B : " + b / 32);
//			System.out.println("enemyMainBaseLocation.getX() : " + enemyMainBaseLocation.getX());
//			System.out.println("enemyMainBaseLocation.getY() : " + enemyMainBaseLocation.getY());

		}

		// System.out.println("BBBB");

		enoughGathered(myUnit, gatherPoint, 0.5);
		


		if (underAttack != null) {
			nextPlace = underAttack;
		} else if (endPoint != null && moveToEndPoint == true && CombatState == StrategyManager.CombatState.defenseMode
				&& underAttack == null) {
			nextPlace = endPoint;
		} else if (gatherPoint != null) {
			nextPlace = gatherPoint;
			System.out.println("모이자");

		}

		return nextPlace;

	}

	
	
	
	
	public void myMutal()
	{
		for(Unit mutal : SM.myCombatUnitType2List)
		{
			
			if(SM.myCombatUnitType2List.size()<6)
			{
				commandUtil.attackMove(mutal, SM.myFirstExpansionLocation.getPosition());
				moveToEndPoint = false;
				return;
			}
			
			Position nextPlace = getNextPlaceToGo(mutal, SM.combatState);
			Unit nextTarget = null;
			nextTarget = getNextTargetOf(mutal);
			
			if(nextTarget!=null)
			{
				mutal.attack(nextTarget);
				
				System.out.println("no target");
				
			}
			else
			{
				commandUtil.attackMove(mutal, nextPlace);
				System.out.println("gogogo");

			}
			
			
			
		}
		
		

	}
	
	
	
	
	
	
	public void MUTAL() {
		
		//if (MyBotModule.Broodwar.getFrameCount() % (24*3) != 0) { return; }
		
		
		
		

		if (CombatStateNow == StrategyManager.CombatState.defenseMode) {
			if (mutalisks.size() > 5) {
				for (Unit mutal : mutalisks) {

					// ArrayList <Unit> units = (ArrayList<Unit>)
					// MyBotModule.Broodwar.getUnitsInRadius(mutal_position, 2 * Config.TILE_SIZE);
					// List <Unit> nearbyunits = mutal.getUnitsInRadius(1000);

					ArrayList<Unit> nearbyunits;

					if (enemyMainBaseLocation != null) {
						//// system.out.println("done");

						int x, y;

						x = enemyMainBaseLocation.getX();
						y = enemyMainBaseLocation.getY();

						if (x < 63 * 32) {
							x = 0;
						} else {
							x = 127 * 32;
						}

						if (y < 63 * 32) {
							y = 0;
						} else {
							y = 127 * 32;
						}

						// 0623 nearbyunits = (ArrayList<Unit>)
						// MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 4
						// * Config.TILE_SIZE);
						nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(new Position(x, y),
								4 * Config.TILE_SIZE);

					} else {
						nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(
								new Position(63 * Config.TILE_SIZE, 63 * Config.TILE_SIZE), 4 * Config.TILE_SIZE);

					}
					// ArrayList <Unit> nearbyunits = (ArrayList<Unit>)
					// MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 5
					// * Config.TILE_SIZE);

					ArrayList<Unit> nearbyenemy = new ArrayList<Unit>(); // 0622 여기서 초기화 안하면 에러남
					ArrayList<Unit> nearbyenemyworker = new ArrayList<Unit>();

					for (Unit unit222 : nearbyunits) {
						if (unit222.getType() == InformationManager.Instance().getWorkerType(enemyRace)) {
							nearbyenemyworker.add(unit222);
							//// system.out.println(unit222.getType());
						}

						if (unit222.getPlayer() == enemyPlayer && unit222 != null && unit222.exists()
								&& unit222.isAttacking()) {
							nearbyenemy.add(unit222);
							// system.out.println("attackable : " + unit222.getType());
						}
					}

					double minDistance = 1000000000;
					double tempDistance = 0;
					Unit myAttackTarget = null;

					if (nearbyenemy.size() == 0) {
						for (Unit targetENEMY : nearbyenemyworker) {
							if (targetENEMY == null || targetENEMY.exists() == false) {
								continue;
							}

							tempDistance = mutal.getDistance(targetENEMY.getPosition());
							if (minDistance > tempDistance) {
								minDistance = tempDistance;
								myAttackTarget = targetENEMY;
							}
						}
					} else {
						for (Unit targetENEMY : nearbyenemy) {
							if (targetENEMY == null || targetENEMY.exists() == false) {
								continue;
							}

							tempDistance = mutal.getDistance(targetENEMY.getPosition());
							if (minDistance > tempDistance) {
								minDistance = tempDistance;
								myAttackTarget = targetENEMY;
							}

						}
					}

					// 0623 타겟을 이렇게 잡으니 나중에 거의 승리했을때 주변에 딱히 칠 유닛이 없어서 건물도 안치고 노는 문제 발생

					if (mutal.canAttack() && myAttackTarget != null) {

						if (myAttackTarget.getType() == UnitType.Zerg_Spore_Colony
								|| myAttackTarget.getType() == UnitType.Terran_Missile_Turret
								|| myAttackTarget.getType() == UnitType.Protoss_Photon_Cannon) {
							if (enemyMainBaseLocation != null) {

								int x, y;

								x = enemyMainBaseLocation.getX();
								y = enemyMainBaseLocation.getY();

								if (x < 63 * 32) {
									x = 0;
								} else {
									x = 127 * 32;
								}

								if (y < 63 * 32) {
									y = 0;
								} else {
									y = 127 * 32;
								}

								// 0623 mutal.move(new Position(x* Config.TILE_SIZE,y* Config.TILE_SIZE)); //
								// 0622 이렇게 무시하고 강제이동하면 되긴하는데 그 위치에 있으면 어떡할까
								// 이럴거면 이러지말고 아예 누
								MUTAL_MOVEMENT(mutal, null, new Position(x, y));

							}
						} else {
							// 0623 mutal.attack(myAttackTarget); // 0622 이거 포문 감싸기전에는 이거 한줄

							///////////////////////////////////////////////// 0623 적 본진 알고, 방어건물이 있었지만 무시했는데
							///////////////////////////////////////////////// 눈앞에 다른 적이 있는 경우, 이 경우 그대로 두면
							///////////////////////////////////////////////// 계속 본진을 갖다박으므로 중간집결지를 지향하는 어택땅을
							///////////////////////////////////////////////// 하는 것이다.
							int mutal_x = 63 * Config.TILE_SIZE;
							int mutal_y = 63 * Config.TILE_SIZE;

							if (enemyMainBaseLocation != null) {
								mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
								mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

								// system.out.println(enemyMainBaseLocation.getX());
								// system.out.println("case 1 mutal_x : " + mutal_x);

								if (mutal_x > 50 * 32 && mutal_x < 70 * 32) {
									mutal_x = enemyMainBaseLocation.getX();
									// system.out.println("case 1 mutal_x AFTER : " + mutal_x);
									// system.out.println("case 1 mutal_y AFTER : " + mutal_y);
								}
							}
							/////////////////////////////////////////////////////////////

							MUTAL_MOVEMENT(mutal, myAttackTarget, new Position(mutal_x, mutal_y));
						}

					} else {
						// mutal.move(enemyMainBaseLocation.getPosition());

						int mutal_x = 63 * 32;
						int mutal_y = 63 * 32;

						if (enemyMainBaseLocation != null) {
							mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
							mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

							// system.out.println(enemyMainBaseLocation.getX());
							// system.out.println("case 2 mutal_x : " + mutal_x);

							if (mutal_x > 50 * 32 && mutal_x < 70 * 32) {
								mutal_x = enemyMainBaseLocation.getX();
								// system.out.println("case 2 mutal_x AFTER : " + mutal_x);
								// system.out.println("case 2 mutal_y AFTER : " + mutal_y);
							}

						}

						Position mutal_position = new Position(mutal_x, mutal_y);

						ArrayList<Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position,
								1 * Config.TILE_SIZE);

						/*
						 * if(units.isEmpty() == false) { Iterator <Unit> itr = units.iterator();
						 * 
						 * 
						 * while(itr.hasNext()) { Unit unit = itr.next();
						 * 
						 * if(unit.getPlayer() == enemyPlayer) { units.remove(unit); continue; }
						 * 
						 * if(unit.getType() != UnitType.Zerg_Mutalisk) { units.remove(unit); continue;
						 * }o } }
						 */

						if (units.size() > 5 && Mutal_flag == false) {
							Mutal_flag = true;
						}

						if (enemyMainBaseLocation != null && Mutal_flag == true) {

							// 0623 commandUtil.attackMove(mutal, enemyMainBaseLocation.getPosition());
							// 0623 MUTAL_MOVEMENT(mutal, null, enemyMainBaseLocation.getPosition());

							int x, y;

							x = enemyMainBaseLocation.getX();
							y = enemyMainBaseLocation.getY();

							if (x < 63 * 32) {
								x = 0;
							} else {
								x = 127 * 32;
							}

							if (y < 63 * 32) {
								y = 0;
							} else {
								y = 127 * 32;
							}

							// 0623 mutal.move(new Position(x* Config.TILE_SIZE,y* Config.TILE_SIZE)); //
							// 0622 이렇게 무시하고 강제이동하면 되긴하는데 그 위치에 있으면 어떡할까
							// 이럴거면 이러지말고 아예 누
							MUTAL_MOVEMENT(mutal, null, new Position(x, y));

							// system.out.println(x + " , " + y);

						} else {

							// 0623 mutal.move(mutal_position); // 0622 어택무브해야돼 말아야돼
							MUTAL_MOVEMENT(mutal, null, mutal_position);

						}

					}
				}
			}
		}
	}

	void MUTAL_MOVEMENT(Unit mutal, Unit target, Position position) {

		if (MyBotModule.Broodwar.getFrameCount() % (24 * 2.5) != 0) {
			return;
		}

		if (enemyMainBaseLocation != null && Mutal_flag == true) // 일꾼이거나 방어건물이 아닌 공격중인 어떤 적군유닛
		{

			List<Unit> arrived = MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(),
					8 * Config.TILE_SIZE);
			// 0623 죽은 뮤탈 빼는 과정이 필요할듯?

			int mutal_x = 63 * 32;
			int mutal_y = 63 * 32;

			if (enemyMainBaseLocation != null) {
				mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
				mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

				// system.out.println(enemyMainBaseLocation.getX());
				// system.out.println("case 3 mutal_x : " + mutal_x);

				if (mutal_x > 50 * 32 && mutal_x < 70 * 32) {
					mutal_x = enemyMainBaseLocation.getX();
					// system.out.println("case 3 mutal_x AFTER : " + mutal_x);
					// system.out.println("case 3 mutal_y AFTER : " + mutal_y);
				}
			}

			Position mutal_position = new Position(mutal_x, mutal_y);

			ArrayList<Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position,
					1 * Config.TILE_SIZE);

			ArrayList<Unit> survived_MUTAL = (ArrayList<Unit>) MyBotModule.Broodwar
					.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);

			Iterator<Unit> itr = survived_MUTAL.iterator();
			int survived_Mutal = 0;

			while (itr.hasNext()) {
				Unit unit = itr.next();

				if (unit.getPlayer() == myPlayer && unit.getType() == UnitType.Zerg_Mutalisk) {
					survived_Mutal++;
				}
			}

			/*
			 * 
			 * boolean temp_Mutal_flag = false;
			 * 
			 * 
			 * if(units.size()>5) { temp_Mutal_flag = true; }
			 */

			if (units.size() > 5 && Mutal_flag == false) {
				Mutal_flag = true;
			}

			if (!Mutal_flag) {
				mutal.move(mutal_position);
				// system.out.println("정찰이 늦어서 나중에 본진 찾고 일단 중간집결지로 가는 중이다");
			} else if (!arrived.contains(mutal)) {
				mutal.move(position);
				// system.out.println("아직 더 가야됨");
			}

			else if (survived_Mutal < 4) {
				Mutal_flag = false;
				mutal.move(mutal_position);
				// system.out.println("후퇴하자");

			}

			else {
				commandUtil.attackMove(mutal, position);
				// system.out.println("다 왔습니다."); // 여기서도 누굴 칠까 우선순위가 필요
			}

		} else if (target == null) {
			// mutal.move(position); // 중간 집결이거나, 적본진으로 가는데 방어건물 등이 있어서 무시하고 쭉 들어가는 경우

			if (enemyMainBaseLocation != null) {
				List<Unit> arrived = MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(),
						8 * Config.TILE_SIZE);

				int mutal_x = 63 * 32;
				int mutal_y = 63 * 32;

				if (enemyMainBaseLocation != null) {
					mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX()) / 2;
					mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY()) / 2;

					// system.out.println(enemyMainBaseLocation.getX());
					// system.out.println("case 4 mutal_x : " + mutal_x);

					if (mutal_x > 50 * 32 && mutal_x < 70 * 32) {
						mutal_x = enemyMainBaseLocation.getX();
						// system.out.println("case 4 mutal_x AFTER : " + mutal_x);
						// system.out.println("case 4 mutal_y AFTER : " + mutal_y);
					}
				}

				Position mutal_position = new Position(mutal_x, mutal_y);

				ArrayList<Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position,
						1 * Config.TILE_SIZE);

				ArrayList<Unit> survived_MUTAL = (ArrayList<Unit>) MyBotModule.Broodwar
						.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);

				Iterator<Unit> itr = survived_MUTAL.iterator();
				int survived_Mutal = 0;

				while (itr.hasNext()) {
					Unit unit = itr.next();

					if (unit.getPlayer() == myPlayer && unit.getType() == UnitType.Zerg_Mutalisk) {
						survived_Mutal++;
					}
				}

				if (units.size() > 5 && Mutal_flag == false) {
					Mutal_flag = true;
				}

				if (!Mutal_flag) {
					mutal.move(mutal_position);
					// system.out.println("정찰이 빨리 된 편이라서 일단 모이러 갑니다일수도 있고 너무 늦은것일수도 잇다??");
				}

				else if (!arrived.contains(mutal)) {
					mutal.move(position);
					// system.out.println("아직 더 가야됨ㅠㅠ");
				}

				else if (survived_Mutal < 4) {
					Mutal_flag = false;
					mutal.move(mutal_position);
					// system.out.println("후퇴하자");

				}

				else {
					commandUtil.attackMove(mutal, position);
					// system.out.println("다 왔습니다.ㅠㅠ");
				}
			} else {
				// mutal.move(position);
				commandUtil.attackMove(mutal, position);
				// system.out.println("정찰이 덜 되서 일단 갑니다. 아마도 중앙. 그럼 이걸 어택땅으로 하는게 어떨까?");
				// system.out.println("BBB");
			}

		} else if (mutal.canAttack(target)) {
			mutal.attack(target);
			// system.out.println("CCC");
		} else {
			double angle = mutal.getAngle();
			angle = (angle + 3.1416) % 6.2832;

			int x1 = mutal.getPoint().getX();
			int y1 = mutal.getPoint().getY();

			int x2 = target.getPoint().getX();
			int y2 = target.getPoint().getX();

			double a = (y2 - y1) / (x2 - x1);
			double b = a * x1 - y1;

			int x3 = x1;

			if (x1 < x2) {
				x3 = x3 - 1000;
			} else {
				x3 = x3 + 1000;
			}

			int y3 = (int) (a * x3 + b);

			mutal.move(new Position(x3, y3));
			// system.out.println("DDD");

		}

	}
	
	/*
	private static Mutalisk instance = new Mutalisk();

	/// static singleton 객체를 리턴합니다
	public static Mutalisk Instance() {
		return instance;
	}
	*/
	

}
