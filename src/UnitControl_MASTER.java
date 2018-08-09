

import java.util.*;


import bwapi.*;
import bwta.*;

public class UnitControl_MASTER {

	UnitControl_Mutalisk Mutalisk;// = new UnitControl_Mutalisk();
	UnitControl_Hydralisk Hydralisk;
	UnitControl_Overlord Overlord;
	UnitControl_Lurker Lurker;
	UnitControl_Zergling Zergling;
	UnitControl_Defiler Defiler;
	
	public void update() {
		
		if(StrategyManager.Instance().enemyMainBaseLocation!=null 
				&& StrategyManager.Instance().combatState != StrategyManager.CombatState.eliminateEnemy
				)//&& MyBotModule.Broodwar.getFrameCount() % 2 == 0)
		{
			Mutalisk = new UnitControl_Mutalisk();
			Mutalisk.update();	
			
			Hydralisk = new UnitControl_Hydralisk();
			Hydralisk.update();	
			
			Lurker = new UnitControl_Lurker();
			Lurker.update();
			
			Zergling = new UnitControl_Zergling();
			Zergling.update();
			
			Defiler = new UnitControl_Defiler();
			Defiler.update();
			
			
		}
		
		if(StrategyManager.Instance().combatState != StrategyManager.CombatState.eliminateEnemy)
		{
			Overlord = new UnitControl_Overlord();
			Overlord.update();
		}

	}
	
	
}








/*
/// 러커 유닛에 대해 컨트롤 명령을 내립니다
boolean controlLurkerUnitType(Unit unit) {

	boolean hasCommanded = false;

	// defenseMode 일 경우
	if (combatState == CombatState.defenseMode) {

		// 아군 방어 건물이 세워져있는 위치 주위에 버로우시켜놓는다
		Position myDefenseBuildingPosition = null;
		switch (seedPositionStrategyOfMyDefenseBuildingType) {
		case MainBaseLocation:
			myDefenseBuildingPosition = myMainBaseLocation.getPosition();
			break;
		case FirstChokePoint:
			myDefenseBuildingPosition = myFirstChokePoint.getCenter();
			break;
		case FirstExpansionLocation:
			myDefenseBuildingPosition = myFirstExpansionLocation.getPosition();
			break;
		case SecondChokePoint:
			myDefenseBuildingPosition = mySecondChokePoint.getCenter();
			break;
		default:
			myDefenseBuildingPosition = myMainBaseLocation.getPosition();
			break;
		}

		if (myDefenseBuildingPosition != null) {
			if (unit.isBurrowed() == false) {
				if (unit.getDistance(myDefenseBuildingPosition) < 5 * Config.TILE_SIZE) {
					unit.burrow();
					hasCommanded = true;
				}
			}
		}
	}
	// defenseMode 가 아닐 경우
	else {

		// 근처에 적 유닛이 있으면 버로우 시키고, 없으면 언버로우 시킨다
		Position nearEnemyUnitPosition = null;
		double tempDistance = 0;
		for (Unit enemyUnit : MyBotModule.Broodwar.enemy().getUnits()) {

			if (enemyUnit.isFlying())
				continue;

			tempDistance = unit.getDistance(enemyUnit.getPosition());
			if (tempDistance < 6 * Config.TILE_SIZE) {
				nearEnemyUnitPosition = enemyUnit.getPosition();
			}
		}

		if (unit.isBurrowed() == false) {

			if (nearEnemyUnitPosition != null) {
				unit.burrow();
				hasCommanded = true;
			}
		} else {
			if (nearEnemyUnitPosition == null) {
				unit.unburrow();
				hasCommanded = true;
			}
		}
	}

	return hasCommanded;
}
*/
