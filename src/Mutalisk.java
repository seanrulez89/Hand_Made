import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;




public class Mutalisk 
{
	Player myPlayer;
	Race myRace;

	Player enemyPlayer;
	Race enemyRace;
	
	BaseLocation myMainBaseLocation;
	BaseLocation enemyMainBaseLocation;
	
	private CommandUtil commandUtil = new CommandUtil();
	StrategyManager SM = StrategyManager.Instance();
	
	StrategyManager.CombatState CombatStateNow = SM.combatState;
	ArrayList <Unit> mutalisks = SM.myCombatUnitType2List;


	boolean Mutal_flag;
	
	
	
	public void MUTAL ()
	{
/*		
		if (MyBotModule.Broodwar.getFrameCount() % (24*3) != 0) {
			return;
		}
*/	//	
		
		
		if (CombatStateNow == StrategyManager.CombatState.defenseMode)
		{
			if(mutalisks.size() > 5)
			{
				for(Unit mutal : mutalisks)
				{
					
					//						ArrayList <Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position, 2 * Config.TILE_SIZE);
					//List <Unit> nearbyunits = mutal.getUnitsInRadius(1000);
					
					ArrayList <Unit> nearbyunits;
					
					
					
					
					if(enemyMainBaseLocation != null)
					{
						//System.out.println("done");
						
						int x, y;
						
						x = enemyMainBaseLocation.getX();
						y = enemyMainBaseLocation.getY();
						
						if(x<63*32) {
							x=0;
						}
						else {
							x=127*32;
						}
						
						if(y<63*32) {
							y=0;
						}
						else {
							y=127*32;
						}
						
						
						
						
//0623						nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 4 * Config.TILE_SIZE);
						nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(new Position(x,y), 4 * Config.TILE_SIZE);
						
					}
					else 
					{
						nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(new Position(63 * Config.TILE_SIZE ,63 * Config.TILE_SIZE), 4 * Config.TILE_SIZE);
						
					}
//					ArrayList <Unit> nearbyunits = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 5 * Config.TILE_SIZE);
					
					
					ArrayList <Unit> nearbyenemy = new ArrayList <Unit>(); // 0622 여기서 초기화 안하면 에러남
					ArrayList <Unit> nearbyenemyworker = new ArrayList <Unit>();

					for(Unit unit222 : nearbyunits)
					{
						if(unit222.getType() == InformationManager.Instance().getWorkerType(enemyRace))
						{
							nearbyenemyworker.add(unit222);
							//System.out.println(unit222.getType());
						}
						
						
						if(unit222.getPlayer() == enemyPlayer && unit222 != null && unit222.exists() && unit222.isAttacking())
						{
							nearbyenemy.add(unit222);
							System.out.println("attackable : " + unit222.getType());
						}	
					}
					
					
					double minDistance = 1000000000;
					double tempDistance = 0;
					Unit myAttackTarget = null;
					
					
					if(nearbyenemy.size() == 0)
					{
						for(Unit targetENEMY : nearbyenemyworker) 
						{			
							if(targetENEMY == null || targetENEMY.exists() == false)
							{
								continue;
							}
							
							tempDistance = mutal.getDistance(targetENEMY.getPosition());
							if (minDistance > tempDistance) 
							{
								minDistance = tempDistance;
								myAttackTarget = targetENEMY;
							}	
						}	
					}
					else
					{
						for(Unit targetENEMY : nearbyenemy) 
						{			
							if(targetENEMY == null || targetENEMY.exists() == false)
							{
								continue;
							}
							
							tempDistance = mutal.getDistance(targetENEMY.getPosition());
							if (minDistance > tempDistance) 
							{
								minDistance = tempDistance;
								myAttackTarget = targetENEMY;
							}
						
						}
					}
					
					// 0623 타겟을 이렇게 잡으니 나중에 거의 승리했을때 주변에 딱히 칠 유닛이 없어서 건물도 안치고 노는 문제 발생
					

					
					if (mutal.canAttack() && myAttackTarget != null)
					{
						
						if(myAttackTarget.getType() == UnitType.Zerg_Spore_Colony || myAttackTarget.getType() == UnitType.Terran_Missile_Turret || myAttackTarget.getType() == UnitType.Protoss_Photon_Cannon)
						{
							if(enemyMainBaseLocation != null)
							{
								
								int x, y;
								
								x = enemyMainBaseLocation.getX();
								y = enemyMainBaseLocation.getY();
								
								if(x<63*32) {
									x=0;
								}
								else {
									x=127*32;
								}
								
								if(y<63*32) {
									y=0;
								}
								else {
									y=127*32;
								}
								
								
//0623							mutal.move(new Position(x* Config.TILE_SIZE,y* Config.TILE_SIZE));	 // 0622 이렇게 무시하고 강제이동하면 되긴하는데 그 위치에 있으면 어떡할까
								// 이럴거면 이러지말고 아예 누
								MUTAL_MOVEMENT(mutal, null, new Position(x, y));
								
							}
						}
						else 
						{
//0623						mutal.attack(myAttackTarget); // 0622  이거 포문 감싸기전에는 이거 한줄
							
							
							///////////////////////////////////////////////// 0623 적 본진 알고, 방어건물이 있었지만 무시했는데 눈앞에 다른 적이 있는 경우, 이 경우 그대로 두면 계속 본진을 갖다박으므로 중간집결지를 지향하는 어택땅을 하는 것이다.
							int mutal_x = 63 * Config.TILE_SIZE;
							int mutal_y = 63 * Config.TILE_SIZE;
							
							if(enemyMainBaseLocation != null)
							{
								mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX())/2;
								mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY())/2;
								
								System.out.println(enemyMainBaseLocation.getX());
								System.out.println("case 1 mutal_x : " + mutal_x);
								
								if(mutal_x>50 *32 && mutal_x<70*32)
								{
									mutal_x = enemyMainBaseLocation.getX();
									System.out.println("case 1 mutal_x AFTER : " + mutal_x);
									System.out.println("case 1 mutal_y AFTER : " + mutal_y);
								}
							}
							/////////////////////////////////////////////////////////////
							
														
							MUTAL_MOVEMENT(mutal, myAttackTarget, new Position(mutal_x, mutal_y));
						}
						
						
						
						
						
					}
					else 
					{
//						mutal.move(enemyMainBaseLocation.getPosition());


						
						
						int mutal_x = 63 * 32;
						int mutal_y = 63 * 32;
						
						if(enemyMainBaseLocation != null)
						{
							mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX())/2;
							mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY())/2;
							
							System.out.println(enemyMainBaseLocation.getX());
							System.out.println("case 2 mutal_x : " + mutal_x);
							
							if(mutal_x>50 *32 && mutal_x<70 * 32)
							{
								mutal_x = enemyMainBaseLocation.getX();
								System.out.println("case 2 mutal_x AFTER : " + mutal_x);
								System.out.println("case 2 mutal_y AFTER : " + mutal_y);	
							}
							
							
						}
						
						
						Position mutal_position = new Position(mutal_x, mutal_y);
						
						
						
						
						
						

						ArrayList <Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position, 1 * Config.TILE_SIZE);
						
						
/*						
						if(units.isEmpty() == false) 
						{
							Iterator <Unit> itr = units.iterator();
							
							
							while(itr.hasNext())
							{
								Unit unit = itr.next();
								
								if(unit.getPlayer() == enemyPlayer)
								{
									units.remove(unit);
									continue;
								}
								
								if(unit.getType() != UnitType.Zerg_Mutalisk)
								{
									units.remove(unit);
									continue;
								}o
							}
						}
*/
						
						if(units.size()>5 && Mutal_flag == false)
						{
							Mutal_flag = true;
						}
						
						
							
						if(enemyMainBaseLocation != null && Mutal_flag == true )
						{
							
//0623						commandUtil.attackMove(mutal, enemyMainBaseLocation.getPosition());
//0623						MUTAL_MOVEMENT(mutal, null, enemyMainBaseLocation.getPosition());
							
							int x, y;
							
							x = enemyMainBaseLocation.getX();
							y = enemyMainBaseLocation.getY();
							
							if(x<63*32) {
								x=0;
							}
							else {
								x=127*32;
							}
							
							if(y<63*32) {
								y=0;
							}
							else {
								y=127*32;
							}
							
							
//0623							mutal.move(new Position(x* Config.TILE_SIZE,y* Config.TILE_SIZE));	 // 0622 이렇게 무시하고 강제이동하면 되긴하는데 그 위치에 있으면 어떡할까
							// 이럴거면 이러지말고 아예 누
							MUTAL_MOVEMENT(mutal, null, new Position(x, y));
							
							System.out.println(x + " , " + y);
							
							
							
							

						}
						else
						{
							
//0623						mutal.move(mutal_position); // 0622 어택무브해야돼 말아야돼
							MUTAL_MOVEMENT(mutal, null, mutal_position);
							
						}	
					
						

					
						
						
						
						
					}
				}
			}
		}
	}

	
	void MUTAL_MOVEMENT(Unit mutal, Unit target, Position position)
	{
		
		if (MyBotModule.Broodwar.getFrameCount() % (24*2.5) != 0) {
			return;
		}
		
		if(enemyMainBaseLocation != null && Mutal_flag == true) // 일꾼이거나 방어건물이 아닌 공격중인 어떤 적군유닛
		{
			
			List <Unit> arrived = MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);
			// 0623 죽은 뮤탈 빼는 과정이 필요할듯?

			
			int mutal_x = 63 * 32;
			int mutal_y = 63 * 32;
			
			if(enemyMainBaseLocation != null)
			{
				mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX())/2;
				mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY())/2;
				
				System.out.println(enemyMainBaseLocation.getX());
				System.out.println("case 3 mutal_x : " + mutal_x);
				
				if(mutal_x>50 *32 && mutal_x<70 * 32)
				{
					mutal_x = enemyMainBaseLocation.getX();
					System.out.println("case 3 mutal_x AFTER : " + mutal_x);
					System.out.println("case 3 mutal_y AFTER : " + mutal_y);
				}
			}
			
			Position mutal_position = new Position(mutal_x, mutal_y);
	
			ArrayList <Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position, 1 * Config.TILE_SIZE);
			
			ArrayList <Unit> survived_MUTAL = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);
			
			Iterator <Unit> itr = survived_MUTAL.iterator();
			int survived_Mutal = 0;
			
			while(itr.hasNext())
			{
				Unit unit = itr.next();
				
				if(unit.getPlayer() == myPlayer && unit.getType() == UnitType.Zerg_Mutalisk)
				{
					survived_Mutal++;
				}
			}
			
			
			
			/*
			
			boolean temp_Mutal_flag = false;
					
			
			if(units.size()>5)
			{
				temp_Mutal_flag = true;
			}
			*/
			
			if(units.size()>5 && Mutal_flag == false)
			{
				Mutal_flag = true;
			}
			
			
			
			
			if(!Mutal_flag)
			{
				mutal.move(mutal_position);
				System.out.println("정찰이 늦어서 나중에 본진 찾고 일단 중간집결지로 가는 중이다");
			}
			else if(!arrived.contains(mutal))
			{
				mutal.move(position);
				System.out.println("아직 더 가야됨");
			}
			
			else if (survived_Mutal<4)
			{
				Mutal_flag = false;
				mutal.move(mutal_position);
				System.out.println("후퇴하자");
				
			}
			
			
			
			else {
				commandUtil.attackMove(mutal, position);
				System.out.println("다 왔습니다."); // 여기서도 누굴 칠까 우선순위가 필요
			}
			
			
			
			
		}
		else if(target==null)
		{
//			mutal.move(position); // 중간 집결이거나, 적본진으로 가는데 방어건물 등이 있어서 무시하고 쭉 들어가는 경우

			if(enemyMainBaseLocation != null)
			{
				List <Unit> arrived = MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);
				
				int mutal_x = 63 * 32;
				int mutal_y = 63 * 32;
				
				if(enemyMainBaseLocation != null)
				{
					mutal_x = (enemyMainBaseLocation.getX() + myMainBaseLocation.getX())/2;
					mutal_y = (enemyMainBaseLocation.getY() + myMainBaseLocation.getY())/2;
					
					System.out.println(enemyMainBaseLocation.getX());
					System.out.println("case 4 mutal_x : " + mutal_x);
					
					if(mutal_x>50 *32 && mutal_x<70 * 32)
					{
						mutal_x = enemyMainBaseLocation.getX();
						System.out.println("case 4 mutal_x AFTER : " + mutal_x);
						System.out.println("case 4 mutal_y AFTER : " + mutal_y);
					}
				}
				
				Position mutal_position = new Position(mutal_x, mutal_y);
		
				ArrayList <Unit> units = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(mutal_position, 1 * Config.TILE_SIZE);
				
				ArrayList <Unit> survived_MUTAL = (ArrayList<Unit>) MyBotModule.Broodwar.getUnitsInRadius(enemyMainBaseLocation.getPosition(), 8 * Config.TILE_SIZE);
				
				Iterator <Unit> itr = survived_MUTAL.iterator();
				int survived_Mutal = 0;
				
				while(itr.hasNext())
				{
					Unit unit = itr.next();
					
					if(unit.getPlayer() == myPlayer && unit.getType() == UnitType.Zerg_Mutalisk)
					{
						survived_Mutal++;
					}
				}
				
				
				
				
				if(units.size()>5 && Mutal_flag == false)
				{
					Mutal_flag = true;
				}
				
				
				
				
				if(!Mutal_flag)
				{
					mutal.move(mutal_position);
					System.out.println("정찰이 빨리 된 편이라서 일단 모이러 갑니다일수도 있고 너무 늦은것일수도 잇다??");
				}
				
				else if(!arrived.contains(mutal))
				{
					mutal.move(position);
					System.out.println("아직 더 가야됨ㅠㅠ");
				}
				
				else if (survived_Mutal<4)
				{
					Mutal_flag = false;
					mutal.move(mutal_position);
					System.out.println("후퇴하자");
					
				}
				
				
				
				
				
				else {
					commandUtil.attackMove(mutal, position);
					System.out.println("다 왔습니다.ㅠㅠ");
				}
			}
			else
			{
//				mutal.move(position);
				commandUtil.attackMove(mutal, position);
				System.out.println("정찰이 덜 되서 일단 갑니다. 아마도 중앙. 그럼 이걸 어택땅으로 하는게 어떨까?");
				System.out.println("BBB");
			}

			

		}
		else if(mutal.canAttack(target))
		{
			mutal.attack(target);
			System.out.println("CCC");
		}
		else
		{
			double angle = mutal.getAngle();
			angle = (angle + 3.1416)%6.2832;
			
			int x1 = mutal.getPoint().getX();
			int y1 = mutal.getPoint().getY();
			
			int x2 = target.getPoint().getX();		
			int y2 = target.getPoint().getX();
			
			double a = (y2-y1)/(x2-x1);
			double b = a*x1 - y1;
			
			int x3 = x1;
			
			if (x1 < x2)
			{
				x3 = x3 - 1000;
			}
			else
			{
				x3 = x3 + 1000;
			}
			
			int y3 = (int) (a*x3 + b);
			
			mutal.move(new Position(x3, y3));
			System.out.println("DDD");
			
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
