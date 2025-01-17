package Trail;



import Trail.RobotPlayer;
import Trail.Util;
import battlecode.common.*;

public class Soldier {
    static int buildType = 0;
    static int type = 0;
    public static void runSoldier(RobotController rc) throws GameActionException {
        if (Trail.RobotPlayer.turnCount < 1500) {
            MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
            // Search for the closest nearby ruin to complete.
            MapInfo curRuin = null;
            int curDist = 9999999;
            for (MapInfo tile : nearbyTiles) {
                // Make sure the ruin is not already complete (has no tower on it)
                if (tile.hasRuin() && rc.senseRobotAtLocation(tile.getMapLocation()) == null) {
                    int checkDist = tile.getMapLocation().distanceSquaredTo(rc.getLocation());
                    if (checkDist < curDist) {
                        curDist = checkDist;
                        curRuin = tile;
                    }
                }
            }

            if (curRuin != null) {
                MapLocation targetLoc = curRuin.getMapLocation();
                Direction dir = rc.getLocation().directionTo(targetLoc);
                if (rc.canMove(dir))
                    rc.move(dir);
                // Mark the pattern we need to draw to build a tower here if we haven't already.
                MapLocation shouldBeMarked = curRuin.getMapLocation().subtract(dir);
                if (type % 2 == 0) {
                    if (rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc) && (type == 0 || type == 2)) {
                        rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                        System.out.println("Trying to build a tower at " + targetLoc);
                    }
                    for (MapInfo patternTile : rc.senseNearbyMapInfos(targetLoc, 8)) {
                        if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY) {
                            boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                            if (rc.canAttack(patternTile.getMapLocation()))
                                rc.attack(patternTile.getMapLocation(), useSecondaryColor);
                        }
                    }
                    if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc) && (type == 0 || type == 2)) {
                        rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                        rc.setTimelineMarker("Tower built", 0, 255, 0);
                        System.out.println("Built a tower at " + targetLoc + "!");
                    }
                    type++;
                }
                else {
                    if (rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc) && (type == 1 || type > 3)) {
                        rc.markTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc);
                        System.out.println("Trying to build a tower at " + targetLoc);
                        type++;
                    }
                    // Fill in any spots in the pattern with the appropriate paint.
                    for (MapInfo patternTile : rc.senseNearbyMapInfos(targetLoc, 8)) {
                        if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY) {
                            boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                            if (rc.canAttack(patternTile.getMapLocation()))
                                rc.attack(patternTile.getMapLocation(), useSecondaryColor);
                        }
                    }
                    // Complete the ruin if we can
                    if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc) && (type == 1 || type > 3)) {
                        rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, targetLoc);
                        rc.setTimelineMarker("Tower built", 0, 255, 0);
                        System.out.println("Built a tower at " + targetLoc + "!");
                    }
                    type++;
                }

            }

            // Move and attack randomly if no objective.
            Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
            MapLocation nextLoc = rc.getLocation().add(dir);
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
            // Try to paint beneath us as we walk to avoid paint penalties.
            // Avoiding wasting paint by re-painting our own tiles.
            MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
            if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())) {
                rc.attack(rc.getLocation());
            }
        }
        else {
            runASoldier(rc);
        }
    }

        public static void runASoldier(RobotController rc) throws GameActionException {


            // Move and attack randomly if no objective.

            Direction dir = Trail.RobotPlayer.directions[Trail.RobotPlayer.rng.nextInt(Trail.RobotPlayer.directions.length)];
            MapLocation nextLoc = rc.getLocation().add(dir);




            if (rc.canMove(dir)) {
                rc.move(dir);
            }


            if (rc.canAttack(nextLoc)) {
                MapInfo nextLocInfo = rc.senseMapInfo(nextLoc);
                if (!nextLocInfo.getPaint().isAlly()) {
                    rc.attack(nextLoc);
                }
            }

        }
}