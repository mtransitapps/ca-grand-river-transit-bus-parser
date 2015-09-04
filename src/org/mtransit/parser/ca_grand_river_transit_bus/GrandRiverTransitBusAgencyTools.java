package org.mtransit.parser.ca_grand_river_transit_bus;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// http://www.regionofwaterloo.ca/en/regionalgovernment/OpenDataHome.asp
// http://www.regionofwaterloo.ca/en/regionalGovernment/GRT_GTFSdata.asp
// http://www.regionofwaterloo.ca/opendatadownloads/GRT_Merged_GTFS.zip
// http://www.regionofwaterloo.ca/opendatadownloads/GRT_GTFS_Winter2015.zip
// http://www.regionofwaterloo.ca/opendatadownloads/GRT_GTFS.zip
// http://www.regionofwaterloo.ca/opendatadownloads/GRT_Daily_GTFS.zip
public class GrandRiverTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-grand-river-transit-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new GrandRiverTransitBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating Grand River Transit bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating Grand River Transit bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // using route short name as route ID
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return routeLongName;
	}

	private static final String AGENCY_COLOR = "0168B3"; // BLUE (PDF SCHEDULE)

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_880091 = "880091";
	private static final String COLOR_009CE0 = "009CE0";
	private static final String COLOR_000000 = "000000";
	private static final String COLOR_0066FF = "0066FF";
	private static final String COLOR_FF3366 = "FF3366";
	private static final String COLOR_003986 = "003986";
	private static final String COLOR_B72700 = "B72700";
	private static final String COLOR_996666 = "996666";
	private static final String COLOR_E09400 = "E09400";
	private static final String COLOR_990033 = "990033";
	private static final String COLOR_009999 = "009999";
	private static final String COLOR_333366 = "333366";
	private static final String COLOR_CC0099 = "CC0099";
	private static final String COLOR_333300 = "333300";
	private static final String COLOR_FF9900 = "FF9900";
	private static final String COLOR_D0BA00 = "D0BA00";
	private static final String COLOR_000099 = "000099";
	private static final String COLOR_089018 = "089018";
	private static final String COLOR_92278F = "92278F";
	private static final String COLOR_999966 = "999966";
	private static final String COLOR_993366 = "993366";
	private static final String COLOR_FF9966 = "FF9966";
	private static final String COLOR_3399CC = "3399CC";
	private static final String COLOR_333333 = "333333";
	private static final String COLOR_FF99CC = "FF99CC";
	private static final String COLOR_666699 = "666699";
	private static final String COLOR_99CC00 = "99CC00";
	private static final String COLOR_CC99CC = "CC99CC";
	private static final String COLOR_CC6600 = "CC6600";
	private static final String COLOR_666666 = "666666";
	private static final String COLOR_669933 = "669933";
	private static final String COLOR_993399 = "993399";
	private static final String COLOR_003333 = "003333";
	private static final String COLOR_FFCC00 = "FFCC00";
	private static final String COLOR_CC3399 = "CC3399";
	private static final String COLOR_3366FF = "3366FF";
	private static final String COLOR_0066CC = "0066CC";
	private static final String COLOR_FF9933 = "FF9933";
	private static final String COLOR_009933 = "009933";
	private static final String COLOR_CC0000 = "CC0000";
	private static final String COLOR_000066 = "000066";
	private static final String COLOR_0099FF = "0099FF";
	private static final String COLOR_666633 = "666633";
	private static final String COLOR_9900CC = "9900CC";
	private static final String COLOR_FFCC33 = "FFCC33";
	private static final String COLOR_0099CC = "0099CC";

	@Override
	public String getRouteColor(GRoute gRoute) {
		int rsn = Integer.parseInt(gRoute.getRouteShortName());
		switch (rsn) {
		// @formatter:off
		case 1: return COLOR_0099CC;
		case 2: return COLOR_FFCC33;
		case 3: return COLOR_9900CC;
		case 4: return COLOR_666633;
		case 5: return COLOR_0099FF;
		case 6: return COLOR_000066;
		case 7: return COLOR_CC0000;
		case 8: return COLOR_009933;
		case 9: return COLOR_FF9933;
		case 10: return COLOR_0066CC;
		case 11: return COLOR_3366FF;
		case 12: return COLOR_CC3399;
		case 13: return COLOR_FFCC00;
		case 14: return COLOR_003333;
		case 15: return COLOR_993399;
		case 16: return COLOR_669933;
		case 17: return COLOR_666666;
		case 19: return COLOR_CC6600;
		case 20: return COLOR_CC99CC;
		case 21: return COLOR_99CC00;
		case 22: return COLOR_666699;
		case 23: return COLOR_FF99CC;
		case 24: return COLOR_333333;
		case 25: return COLOR_3399CC;
		case 27: return COLOR_FF9966;
		case 29: return COLOR_993366;
		case 31: return COLOR_999966;
		case 33: return COLOR_089018;
		case 34: return COLOR_92278F;
		case 51: return COLOR_CC0000;
		case 52: return COLOR_000099;
		case 53: return COLOR_009933;
		case 54: return COLOR_0099CC;
		case 55: return COLOR_993399;
		case 56: return COLOR_D0BA00;
		case 57: return COLOR_FF9900;
		case 58: return COLOR_333300;
		case 59: return COLOR_CC0099;
		case 60: return COLOR_333366;
		case 61: return COLOR_009999;
		case 62: return COLOR_666666;
		case 63: return COLOR_FFCC00;
		case 64: return COLOR_990033;
		case 67: return COLOR_E09400;
		case 72: return COLOR_996666;
		case 73: return COLOR_0099CC;
		case 75: return COLOR_B72700;
		case 76: return COLOR_000066;
		case 91: return null;
		case 92: return COLOR_003986;
		case 110: return COLOR_0066CC;
		case 111: return COLOR_FF3366;
		case 116: return COLOR_669933;
		case 200: return COLOR_0066FF;
		case 201: return COLOR_000000;
		case 202: return COLOR_000000;
		case 203: return COLOR_000000;
		case 204: return COLOR_000000;
		case 888: return null;
		case 889: return null;
		case 9801: return COLOR_009CE0;
		case 9851: return COLOR_009CE0;
		case 9852: return COLOR_003986;
		case 9901: return COLOR_009CE0;
		case 9903: return COLOR_089018;
		case 9904: return COLOR_880091;
		case 9905: return COLOR_B72700;
		case 9931: return COLOR_009CE0;
		case 9932: return COLOR_003986;
		case 9951: return COLOR_009CE0;
		case 9952: return COLOR_003986;
		case 9953: return COLOR_089018;
		case 9954: return COLOR_880091;
		case 9961: return COLOR_009CE0;
		case 9963: return COLOR_089018;
		case 9964: return COLOR_880091;
		case 9983: return COLOR_089018;
		// @formatter:on
		default:
			System.out.printf("\nUnexpected route color %s!\n", gRoute);
			System.exit(-1);
			return null;
		}
	}

	private static final String TO = " to ";
	private static final String VIA = " via ";

	private static final String STANLEY_PARK = "Stanley Pk";
	private static final String FAIRVIEW_PARK_MALL = "Fairview Pk Mall";
	private static final String UNIVERSITY = "University";
	private static final String U_W_DAVIS_CENTER = "UW Davis Ctr";
	private static final String CONESTOGA_MALL = "Conestoga Mall";
	private static final String LACKNER_VICTORIA = "Lackner / Victoria";
	private static final String QUINTE_MORRISON = "Quinte / Morrison";
	private static final String GOLDEN_MEADOW = "Golden Mdw";
	private static final String ROLLING_MEADOWS = "Rolling Mdws";
	private static final String HIGHLAND_VICTORIA_HILLS_MALLS = "Highland / Victoria Hls Malls";
	private static final String CHARLES_ST_TERMINAL = "Charles St Terminal";
	private static final String HIGHLAND_HILLS_MALL = "Highland Hls Mall";
	private static final String COLUMBIA = "Columbia";
	private static final String ERB = "Erb";
	private static final String CONESTOGA_COLLEGE = "Conestoga College";
	private static final String CONESTOGA_COLLEGE_DOON = "Conestoga College Doon";
	private static final String PIONEER_PARK_PLAZA = "Pioneer Pk Plz";
	private static final String MC_CORMICK_C_C = "McCormick CC";
	private static final String LAKE_LOUISE_CONSERVATION = "Lk Louise / Conservation";
	private static final String INDUSTRIAL_LANGS = "Ind / Langs";
	private static final String CAMBRIDGE_CENTRE = "Cambridge Ctr";
	private static final String HESPELER = "Hespeler";
	private static final String AINSLIE = "Ainslie";
	private static final String COLUMBIA_SUNDEW = "Columbia / Sundew";
	private static final String KING_UNIVERSITY = "King / University";
	private static final String ELMIRA = "Elmira";
	private static final String DANIEL_BLOOMINGDALE = "Daniel / Bloomingdale";
	private static final String WATERLOO = "Waterloo";
	private static final String THE_BOARDWALK = "The Boardwalk";
	private static final String FOREST_GLEN = "Forest Glen";
	private static final String FOREST_GLEN_PLAZA = "Forest Glen Plz";
	private static final String AINSLIE_TERMINAL = "Ainslie Terminal";
	private static final String AINSLIE_ST_TERMINAL = "Ainslie St Terminal";
	private static final String SPORTSWORLD_CROSSING = "Sportsworld Xing";
	private static final String HURON = "Huron";
	private static final String BINGEMANS = "Bingemans";
	private static final String UW_SPECIAL_9903 = "UW Special";
	private static final String ST_MARY_S_EXPRESS_9903 = "St. Mary's Express";
	private static final String FAIRVIEW_EXPRESS_9951 = "Fairview Express";
	private static final String FOREST_GLEN_EXPRESS_9951 = "Forest Glen Express";

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (mRoute.id == 1l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(STANLEY_PARK, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 2l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(HIGHLAND_HILLS_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 3l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FOREST_GLEN_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 4l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(THE_BOARDWALK, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 5l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(DANIEL_BLOOMINGDALE, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(THE_BOARDWALK, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 6l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 7l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(WATERLOO, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 8l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(UNIVERSITY, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 9l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(U_W_DAVIS_CENTER, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 10l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE_DOON, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 11l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FOREST_GLEN_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 12l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 13l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(THE_BOARDWALK, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(U_W_DAVIS_CENTER, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 14l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 15l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(LACKNER_VICTORIA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 16l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FOREST_GLEN_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 17l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(LACKNER_VICTORIA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 19l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(HIGHLAND_HILLS_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 20l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(HIGHLAND_VICTORIA_HILLS_MALLS, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 21l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(ELMIRA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 22l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(HIGHLAND_HILLS_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 23l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 24l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(ROLLING_MEADOWS, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 25l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(GOLDEN_MEADOW, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CHARLES_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 27l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(QUINTE_MORRISON, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 29l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(KING_UNIVERSITY, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(THE_BOARDWALK, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 31l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(COLUMBIA_SUNDEW, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 33l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(HURON, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(FOREST_GLEN, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 34l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(BINGEMANS, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 51l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(HESPELER, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 52l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 53l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 54l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 55l) {
			if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
			// }
		} else if (mRoute.id == 57l) {
			if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 58l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 59l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 60l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 61l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE_DOON, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 62l) {
			if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 63l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 67l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(INDUSTRIAL_LANGS, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 72l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(SPORTSWORLD_CROSSING, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 73l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(LAKE_LOUISE_CONSERVATION, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(MC_CORMICK_C_C, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 75l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 76l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(PIONEER_PARK_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 92l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(ERB, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(COLUMBIA, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 110l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FAIRVIEW_PARK_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE_DOON, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 111l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE_DOON, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(AINSLIE_ST_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 116l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FOREST_GLEN_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE_DOON, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 200l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(AINSLIE_TERMINAL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 201l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(FOREST_GLEN_PLAZA, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 202l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(THE_BOARDWALK, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CONESTOGA_MALL, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 203l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, gTrip.getDirectionId());
				return;
			} else {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.id == 9903l) {
			if (gTrip.getDirectionId() == 0) {
				if (UW_SPECIAL_9903.equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(gTrip.getTripHeadsign(), 0);
					return;
				} else if (ST_MARY_S_EXPRESS_9903.equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(gTrip.getTripHeadsign(), 1);
					return;
				}
			}
			System.out.printf("\nUnepected trip to to split %s\n", gTrip);
			System.exit(-1);
		} else if (mRoute.id == 9951l) {
			if (gTrip.getDirectionId() == 0) {
				if (FOREST_GLEN_EXPRESS_9951.equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(gTrip.getTripHeadsign(), 0);
					return;
				} else if (FAIRVIEW_EXPRESS_9951.equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(gTrip.getTripHeadsign(), 1);
					return;
				}
			}
			System.out.printf("\nUnepected trip to to split %s\n", gTrip);
			System.exit(-1);
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^[\\d]+\\s)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^to\\s)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		int indexOfTO = tripHeadsign.toLowerCase(Locale.ENGLISH).indexOf(TO);
		if (indexOfTO >= 0) {
			tripHeadsign = tripHeadsign.substring(indexOfTO + TO.length());
		}
		int indexOfVIA = tripHeadsign.toLowerCase(Locale.ENGLISH).indexOf(VIA);
		if (indexOfVIA >= 0) {
			tripHeadsign = tripHeadsign.substring(0, indexOfVIA);
		}
		tripHeadsign = STARTS_WITH_TO.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public String getStopCode(GStop gStop) {
		return gStop.getStopId(); // using stop ID as stop code
	}
}
