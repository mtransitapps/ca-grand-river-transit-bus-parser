package org.mtransit.parser.ca_grand_river_transit_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirectionType;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

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
		routeLongName = BUS_PLUS.matcher(routeLongName).replaceAll(BUS_PLUS_REPLACEMENT);
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

	private static final String SLASH = " / ";
	private static final String FAIRVIEW = "Fairview";
	private static final String DOWNTOWN = "Downtown";
	private static final String FAIRVIEW_PARK = FAIRVIEW + " Pk";
	private static final String UNIVERSITY = "University";
	private static final String UW = "UW";
	private static final String CONESTOGA_MALL = "Conestoga Mall";
	private static final String UW_CONESTOGA_MALL = UW + SLASH + CONESTOGA_MALL;
	private static final String QUINTE_MORRISON = "Quinte" + SLASH + "Morrison";
	private static final String HIGHLAND_HILLS = "Highland Hls";
	private static final String COLUMBIA = "Columbia";
	private static final String ERB = "Erb";
	private static final String CONESTOGA_COLLEGE = "Conestoga College";
	private static final String PIONEER_PARK = "Pioneer Pk";
	private static final String MC_CORMICK_C_C = "McCormick CC";
	private static final String LAKE_LOUISE_CONSERVATION = "Lk Louise" + SLASH + "Conservation";
	private static final String INDUSTRIAL_SHORT = "Ind";
	private static final String LANGS = "Langs";
	private static final String CAMBRIDGE_CENTRE = "Cambridge Ctr";
	private static final String SAGINAW = "Saginaw";
	private static final String WATERLOO = "Waterloo";
	private static final String WATERLOO_INDUSTRIAL = WATERLOO + " " + INDUSTRIAL_SHORT;
	private static final String THE_BOARDWALK = "The Boardwalk";
	private static final String AINSLIE_TERMINAL = "Ainslie Terminal";
	private static final String SPORTSWORLD = "Sportsworld";
	private static final String HURON = "Huron";
	private static final String BINGEMANS = "Bingemans";
	private static final String BRIDGEPORT = "Bridgeport";
	private static final String WOODSIDE = "Woodside";
	private static final String ST_MARY_S = "St Mary's";
	private static final String FOREST_GLEN = "Forest Gln";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(27l, new RouteTripSpec(27l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, QUINTE_MORRISON, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, FAIRVIEW_PARK) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "1046", "1564", "1651" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "1651", "1658", "1558" })) //
				.compileBothTripSort());
		map2.put(62l, new RouteTripSpec(62l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, WOODSIDE) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "2137", "2143", "1517" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "1518", "2130", "2137" })) //
				.compileBothTripSort());
		map2.put(73l, new RouteTripSpec(73l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, MC_CORMICK_C_C, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, LAKE_LOUISE_CONSERVATION) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "2075", "2081", "3627" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "3628", "3628_merged_1267883347", "3628_merged_1267883273", "2069", "2075" })) //
				.compileBothTripSort());
		map2.put(75l, new RouteTripSpec(75l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, SAGINAW, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "1386", "1345", "3394" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "3394", "1352", "3797" })) //
				.compileBothTripSort());
		map2.put(76l, new RouteTripSpec(76l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, CONESTOGA_COLLEGE, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, PIONEER_PARK) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "1744", "3999", "1732" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "1732", "3957", "1744" })) //
				.compileBothTripSort());
		map2.put(92l, new RouteTripSpec(92l, //
				0, MTrip.HEADSIGN_TYPE_STRING, ERB, //
				1, MTrip.HEADSIGN_TYPE_STRING, COLUMBIA) //
				.addTripSort(0, //
						Arrays.asList(new String[] { "3891", "1992", "1994", "2786", "2787", "1286", "4072", "1972", "3465", "2526", "2670" })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { "2670", "1971", "1972", "4072", "2511", "3162", "3590", "1994" })) //
				.compileBothTripSort());
		map2.put(889l, new RouteTripSpec(92l, //
				0, MTrip.HEADSIGN_TYPE_STRING, SPORTSWORLD, //
				1, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL) //
				.addTripSort(0, //
						Arrays.asList(new String[] { "6101", "6103", "1570" })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { "1571", "3517", "6101" })) //
				.compileBothTripSort());
		map2.put(9903l, new RouteTripSpec(9903l, //
				0, MTrip.HEADSIGN_TYPE_STRING, UW, //
				1, MTrip.HEADSIGN_TYPE_STRING, ST_MARY_S) //
				.addTripSort(0, //
						Arrays.asList(new String[] { "1143", "1150", "1124" })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { "3228", "3474" })) //
				.compileBothTripSort());
		map2.put(9951l, new RouteTripSpec(9951l, //
				0, MTrip.HEADSIGN_TYPE_STRING, FOREST_GLEN, //
				1, MTrip.HEADSIGN_TYPE_STRING, FAIRVIEW) //
				.addTripSort(0, //
						Arrays.asList(new String[] { "1840", "1771" })) //
				.addTripSort(1, //
						Arrays.asList(new String[] { "3474", "1554" })) //
				.compileBothTripSort());

		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()));
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		String gTripHeadsign = gTrip.getTripHeadsign();
		gTripHeadsign = STARTS_WITH_RSN.matcher(gTripHeadsign).replaceAll(StringUtils.EMPTY);
		if (gTripHeadsign.length() > mRoute.getLongName().length()) {
			gTripHeadsign = Pattern.compile("((^|\\W){1}(" + mRoute.getLongName() + ")(\\W|$){1})", Pattern.CASE_INSENSITIVE).matcher(gTripHeadsign)
					.replaceAll(" ");
		}
		int indexOfTO = gTripHeadsign.toLowerCase(Locale.ENGLISH).indexOf(TO);
		if (indexOfTO >= 0) {
			String gTripHeadsignBeforeTO = gTripHeadsign.substring(0, indexOfTO);
			String gTripHeadsignAfterTO = gTripHeadsign.substring(indexOfTO + TO.length());
			if (mRoute.getLongName().equalsIgnoreCase(gTripHeadsignBeforeTO)) {
				gTripHeadsign = gTripHeadsignAfterTO;
			} else if (mRoute.getLongName().equalsIgnoreCase(gTripHeadsignAfterTO)) {
				gTripHeadsign = gTripHeadsignBeforeTO;
			} else {
				gTripHeadsign = gTripHeadsignAfterTO;
			}
		}
		int indexOfVIA = gTripHeadsign.toLowerCase(Locale.ENGLISH).indexOf(VIA);
		if (indexOfVIA >= 0) {
			String gTripHeadsignBeforeVIA = gTripHeadsign.substring(0, indexOfVIA);
			String gTripHeadsignAfterVIA = gTripHeadsign.substring(indexOfVIA + VIA.length());
			if (mRoute.getLongName().equalsIgnoreCase(gTripHeadsignBeforeVIA)) {
				gTripHeadsign = gTripHeadsignAfterVIA;
			} else if (mRoute.getLongName().equalsIgnoreCase(gTripHeadsignAfterVIA)) {
				gTripHeadsign = gTripHeadsignBeforeVIA;
			} else {
				gTripHeadsign = gTripHeadsignBeforeVIA;
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTripHeadsign), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (mTrip.getRouteId() == 2l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 3l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 4l) {
			if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 5l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(BRIDGEPORT, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 7l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(UW_CONESTOGA_MALL, mTrip.getHeadsignId());
				return true;
			} else {
				mTrip.setHeadsignString(FAIRVIEW, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 8l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(UNIVERSITY, mTrip.getHeadsignId());
				return true;
			} else {
				mTrip.setHeadsignString(FAIRVIEW, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 9l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, mTrip.getHeadsignId());
				return true;
			} else {
				mTrip.setHeadsignString(UW, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 12l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(CONESTOGA_MALL, mTrip.getHeadsignId());
				return true;
			} else {
				mTrip.setHeadsignString(FAIRVIEW_PARK, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 13l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(THE_BOARDWALK, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 14l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(WATERLOO_INDUSTRIAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 22l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(HIGHLAND_HILLS, mTrip.getHeadsignId());
				return true;
			} else if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 33l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(HURON, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 34l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(BINGEMANS, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 51l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(AINSLIE_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 67l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(LANGS, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 92l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(ERB, mTrip.getHeadsignId());
				return true;
			} else if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(COLUMBIA, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 200l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(UW_CONESTOGA_MALL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 203l) {
			if (mTrip.getHeadsignId() == 0) {
				mTrip.setHeadsignString(SPORTSWORLD, mTrip.getHeadsignId());
				return true;
			} else if (mTrip.getHeadsignId() == 1) {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE, mTrip.getHeadsignId());
				return true;
			}
		}
		return super.mergeHeadsign(mTrip, mTripToMerge);
	}

	private static final Pattern BUS_PLUS = Pattern.compile("( bus plus$)", Pattern.CASE_INSENSITIVE);
	private static final String BUS_PLUS_REPLACEMENT = " BusPlus";

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^[\\d]+[\\s]*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IXPRESS = Pattern.compile("(^IXpress )", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("( express$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_BUSPLUS = Pattern.compile("( busplus$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_SPECIAL = Pattern.compile("( special$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^to\\s)", Pattern.CASE_INSENSITIVE);

	private static final Pattern INDUSTRIAL = Pattern.compile("(industrial)", Pattern.CASE_INSENSITIVE);
	private static final String INDUSTRIAL_REPLACEMENT = INDUSTRIAL_SHORT;

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
		tripHeadsign = STARTS_WITH_IXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = BUS_PLUS.matcher(tripHeadsign).replaceAll(BUS_PLUS_REPLACEMENT);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_BUSPLUS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_SPECIAL.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = INDUSTRIAL.matcher(tripHeadsign).replaceAll(INDUSTRIAL_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
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
		return String.valueOf(getStopId(gStop)); // using stop ID as stop code
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(GStop gStop) {
		if (!Utils.isDigitsOnly(gStop.getStopId())) {
			Matcher matcher = DIGITS.matcher(gStop.getStopId());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group());
			}
		}
		return super.getStopId(gStop);
	}
}
