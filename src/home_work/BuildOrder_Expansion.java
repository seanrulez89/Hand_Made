package home_work;
import java.util.*;
import java.io.*;

import bwapi.*;
import bwta.*;

public class BuildOrder_Expansion {

	

	public BaseLocation expansion() {
		
		BaseLocation nextEXP = null;

		List<BaseLocation> myBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().selfPlayer);

		List<BaseLocation> enemyBaseLocations = InformationManager.Instance()
				.getOccupiedBaseLocations(InformationManager.Instance().enemyPlayer);

		List<BaseLocation> EXPLocations = BWTA.getBaseLocations();

		double minDistance = 1000000000;
		double distanceFromMyLocation = 0;
		double distanceFromEnemyLocation = 0;

		for (BaseLocation myBaseLocation : myBaseLocations) {
			if (EXPLocations.contains(myBaseLocation)) {
				EXPLocations.remove(myBaseLocation);
			}

		}

		for (BaseLocation enemyBaseLocation : enemyBaseLocations) {
			if (EXPLocations.contains(enemyBaseLocation)) {
				EXPLocations.remove(enemyBaseLocation);
			}
		}

		for (BaseLocation EXPLocation : EXPLocations) {
			
			if(EXPLocation.getGeysers().size()==0)
			{
				continue;
			}
			

			for (BaseLocation myBaseLocation : myBaseLocations) {
				distanceFromMyLocation = EXPLocation.getGroundDistance(myBaseLocation);

				for (BaseLocation enemyBaseLocation : enemyBaseLocations) {
					distanceFromEnemyLocation = EXPLocation.getGroundDistance(enemyBaseLocation);

					if (minDistance > distanceFromMyLocation) {
						if (distanceFromMyLocation < distanceFromEnemyLocation) {
							minDistance = distanceFromMyLocation;
							nextEXP = EXPLocation;
							// System.out.println(nextEXP);
						}

					}
				}
			}
		}
		
		return nextEXP;
	}

	private static BuildOrder_Expansion instance = new BuildOrder_Expansion();

	/// static singleton 객체를 리턴합니다
	public static BuildOrder_Expansion Instance() {
		return instance;
	}

}
