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
import org.mtransit.commons.GrandRiverTransitCommons;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
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
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
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
		if ("Out Of Service".equalsIgnoreCase(gTrip.getTripHeadsign())) {
			return true; // exclude
		}
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		return super.excludeRoute(gRoute);
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
		if (Utils.isUppercaseOnly(routeLongName, true, true)) {
			routeLongName = routeLongName.toLowerCase(Locale.ENGLISH);
		}
		routeLongName = BUS_PLUS.matcher(routeLongName).replaceAll(BUS_PLUS_REPLACEMENT);
		routeLongName = CleanUtils.CLEAN_AND.matcher(routeLongName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
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
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
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
			case 26: return "D0BA00";
			case 27: return COLOR_FF9966;
			case 28: return "C4D600";
			case 29: return COLOR_993366;
			case 31: return COLOR_999966;
			case 33: return COLOR_089018;
			case 34: return COLOR_92278F;
			case 36: return null; // TODO ?
			case 50: return null; // TODO ?
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
			case 77: return "E09400";
			case 78: return "EA4AA3";
			case 91: return "009CE0";
			case 92: return COLOR_003986;
			case 110: return COLOR_0066CC;
			case 111: return COLOR_FF3366;
			case 116: return COLOR_669933;
			case 200: return COLOR_0066FF;
			case 201: return COLOR_000000;
			case 202: return COLOR_000000;
			case 203: return COLOR_000000;
			case 204: return COLOR_000000;
			case 205: return COLOR_000000;
			case 302: return "05AA64";
			case 901: return null; // TODO?
			case 902: return null; // TODO?
			case 9801: return COLOR_009CE0;
			case 9802: return null; // TODO?
			case 9841: return null; // TODO?
			case 9851: return COLOR_009CE0;
			case 9852: return COLOR_003986;
			case 9901: return COLOR_009CE0;
			case 9903: return COLOR_089018;
			case 9904: return COLOR_880091;
			case 9905: return COLOR_B72700;
			case 9922: return null; // TODO?
			case 9931: return COLOR_009CE0;
			case 9932: return COLOR_003986;
			case 9941: return null; // TODO?
			case 9942: return null; // TODO?
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
		return super.getRouteColor(gRoute);
	}

	private static final String _SLASH_ = " / ";

	private static final String INDUSTRIAL_SHORT = "Ind";
	private static final String SECONDARY_SCHOOL_SHORT = "SS";

	private static final String AINSLIE_TERMINAL = "Ainslie Terminal";
	private static final String ARTHUR = "Arthur";
	private static final String BLOCK_LINE = "Block Line";
	private static final String BLOCK_LINE_STATION = BLOCK_LINE + " Sta";
	private static final String BLOOMINGDALE = "Bloomingdale";
	private static final String BRIDGEPORT = "Bridgeport";
	private static final String CAMBRIDGE_CENTRE = "Cambridge Ctr";
	private static final String CAMBRIDGE_CENTRE_STATION = CAMBRIDGE_CENTRE + " Sta";
	private static final String CEDAR = "Cedar";
	private static final String CEDARBRAE = "Cedarbrae";
	private static final String CENTRAL_STATION = "Central" + " Sta";
	private static final String CHANDLER = "Chandler";
	private static final String CHARLES_TERMINAL = "Charles Terminal";
	private static final String CHURCH = "Church";
	private static final String CONESTOGA = "Conestoga";
	private static final String CONESTOGA_COLLEGE = CONESTOGA + " College";
	private static final String CONESTOGA_STATION = CONESTOGA + " Sta";
	private static final String DANIEL = "Daniel";
	private static final String DRIFTWOOD = "Driftwood";
	private static final String ERBSVILLE = "Erbsville";
	private static final String EXAM = "Exam";
	private static final String FAIRWAY_STATION = "Fairway" + " Sta";
	private static final String FISHER_MILLS = "Fisher Mills";
	private static final String FOREST_GLEN = "Forest Gln";
	private static final String FREDERICK_STATION = "Frederick" + " Sta";
	private static final String GLEN_FORREST = "Gln Forrest";
	private static final String GROFF = "Groff";
	private static final String GROH = "Groh";
	private static final String GUELPH = "Guelph";
	private static final String HIGHLAND_HLS = "Highland Hls";
	private static final String HOLIDAY_INN = "Holiday Inn";
	private static final String HOLIDAY_INN_DRIVE = HOLIDAY_INN + " Dr";
	private static final String HURON = "Huron";
	private static final String HURON_HEIGHTS = HURON + " Hts";
	private static final String HURON_VILLAGE = HURON + " Vlg";
	private static final String LAURELWOOD = "Laurelwood";
	private static final String LAURIER = "Laurier";
	private static final String KING = "King";
	private static final String KUMPF = "Kumpf";
	private static final String MC_CORMICK = "McCormick";
	private static final String MELRAN = "Melran";
	private static final String NORTHFIELD = "Northfield";
	private static final String NORTHFIELD_STATION = NORTHFIELD + " Sta";
	private static final String OTTAWA = "Ottawa";
	private static final String RIVER = "River";
	private static final String SAGINAW = "Saginaw";
	private static final String SOUTHWOOD = "Southwood";
	private static final String SPORTSWORLD = "Sportsworld";
	private static final String SPORTSWORLD_STATION = SPORTSWORLD + " Sta";
	private static final String STANLEY_PARK = "Stanley Pk";
	private static final String STRASBURG = "Strasburg";
	private static final String ST_JACOBS_MARKET = "St Jacobs Mkt";
	private static final String ST_MARY_S = "St Mary's";
	private static final String SUNRISE_CTR_STATION = "Sunrise Ctr Sta";
	private static final String THE_BOARDWALK = "The Boardwalk";
	private static final String THE_BOARDWALK_STATION = THE_BOARDWALK + " Sta";
	private static final String TRILLIUM = "Trillium";
	private static final String UNIVERSITY = "University";
	private static final String UNIVERSITY_OF_WATERLOO_SHORT = "UW";
	private static final String UNIVERSITY_OF_WATERLOO_STATION_SHORT = "UW Sta";
	private static final String UNION = "Union";
	private static final String UPTOWN = "Uptown";
	private static final String VILLAGE = "Vlg";
	private static final String WASHBURN = "Washburn";
	private static final String WATERLOO_INDUSTRIAL = "Waterloo " + INDUSTRIAL_SHORT;
	private static final String WATERLOO_PUBLIC_SQUARE_STATION = "Waterloo Public Sq" + " Sta";
	private static final String WEBER = "Weber";
	private static final String WESTHEIGHTS = "Westheights";
	private static final String WESTMOUNT = "Westmount";
	private static final String WOODSIDE = "Woodside";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(26L, new RouteTripSpec(26L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Trillium" + _SLASH_ + "Washburn", //
				1, MTrip.HEADSIGN_TYPE_STRING, BLOCK_LINE_STATION) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"1463", // Block Line Station
								"1781", // Trillium / Washburn
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"1781", // Trillium / Washburn
								"1463", // Block Line Station
						})) //
				.compileBothTripSort());
		map2.put(54L, new RouteTripSpec(54L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Littles Corners", //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1517", "1518", // Ainslie Terminal
								"3535", // ++
								"1528", // ++
								"2225", // Gatehouse / Mcnicho =>
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2225", // Gatehouse / Mcnicho <=
								"2235", // ++
								"2159", // ++
								"1516", "1518", // Ainslie Terminal
						})) //
				.compileBothTripSort());
		map2.put(56L, new RouteTripSpec(56L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE, //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Rose / Argyle") //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1410", // Rose / Argyle
								"1414", //
								"1427", // ==
								"1058", "1065", // Cambridge Centre Station
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1064", "1065", // Cambridge Centre Station
								"1392", // ==
								"1401", //
								"1410", // Rose / Argyle
						})) //
				.compileBothTripSort());
		map2.put(58L, new RouteTripSpec(58L, //
				GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Elgin / Avenue", //
				GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL) //
				.addTripSort(GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1516", // != Ainslie Terminal
								// "1521", // != Ainslie Terminal
								"1605", // ==
								"2100", // ++
								"2108" // Elgin / Avenue
						})) //
				.addTripSort(GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2108", // Elgin / Avenue
								"2116", //
								"2118", // ==
								"2119", // !=
								"2360", // !=
								"2120", // ==
								"2125", // ++
								"1516", "1518" // Ainslie Terminal
						})) //
				.compileBothTripSort());
		map2.put(59L, new RouteTripSpec(59L, //
				GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL, //
				GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Christopher / Myers") //
				.addTripSort(GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2279", // == Christopher / Myers
								"2280", // !=
								"2286", // !=
								"2287", // ==
								"2288", // ==
								"2289", // !==
								"2291", // !=
								"2294", // !==
								"2295", // ==
								"2299", // !+
								"2300", // !=
								"2297", // !=
								"1507", // !=
								"2301", // ==
								"2125", // ==
								"1512", "1519", "1522", // != Ainslie Terminal =>
						})) //
				.addTripSort(GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1519", // Ainslie Terminal
								"2271", //
								"2279" // == Christopher / Myers
						})) //
				.compileBothTripSort());
		map2.put(60L, new RouteTripSpec(60L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Saginaw" + _SLASH_ + "Burnett", //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1058", "1064", // Cambridge Centre Station <=
								"6021", // ==
								"1371", // Saginaw / Burnett =>
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1371", // Saginaw / Burnett <=
								"6103", // ==
								"1058", "1064", // != Cambridge Centre Station =>
						})) //
				.compileBothTripSort());
		map2.put(62L, new RouteTripSpec(62L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL, //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, WOODSIDE) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2290", // != Cedar / Southgate <=
								"2137", // == Southgate / Day <=
								"2138", // == Churchill / Dale
								"2139", // != Hillcrest / Churchill
								"2140", // == Hillcrest / Highland
								"2146", // Kent / Cedar
								"2147", // == Cedar / Southwood
								"2148", // != Cedar / Woodside
								"2149", // ==
								"2151", // != St. Gregorys / St. Andrews
								"2152", // != Stanley / Borden
								"2250", // != Southwood / Cedar
								"2252", // != Southwood / Wedgewood
								"2153", // == Stanley / Tait
								"1517", // Ainslie Terminal
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1518", // Ainslie Terminal
								"2126", // == Grand / Victoria
								"2127", // != Crombie / Grand
								"2128", // == Middleton / Francis
								"2131", // == Stanley / Tait
								"2132", // != Stanley / St. Andrews
								"2136", // != Woodside / Cedar =>
								"2262", // != Fourth / Vincent
								"2266", // != Southwood / Cedar
								"2290", // != Cedar / Southgate
								"2137", // == Southgate / Day =>
						})) //
				.compileBothTripSort());
		map2.put(63L, new RouteTripSpec(63L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE, //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, AINSLIE_TERMINAL) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1522", // Ainslie Terminal
								"2169", // ++
								"1504", // Franklin / Mclaren
								"2174", // !=
								"2176", // != Dundas / South Cambridge Centre =>
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2176", // != Dundas / South Cambridge Centre <=
								"1506", // ==
								"2182", // ++
								"2159", // ==
								"1519", "1520", "1522", // Ainslie Terminal
						})) //
				.compileBothTripSort());
		map2.put(64L, new RouteTripSpec(64L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE, //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Dover / Rose") //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"3520", // Dover / Rose
								"1442", // ==
								"1588", // !=
								"1589", // !=
								"5031", // !=
								"1443", // ==
								"1444", // ++
								"1067" // Cambridge Centre - Bay 8
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1067", // Cambridge Centre - Bay 8
								"1432", // ++
								"3520" // Dover / Rose
						})) //
				.compileBothTripSort());
		map2.put(75L, new RouteTripSpec(75L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, SAGINAW, //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CAMBRIDGE_CENTRE) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1064", // != Cambridge Centre Station
								"1058", // != Cambridge Centre Station
								"1275", // ==
								"1345", //
								"3394" //
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"3394", //
								"1352", //
								"1320", // ==
								"1058", // != Cambridge Centre Station
								"1064", // != Cambridge Centre Station
						})) //
				.compileBothTripSort());
		map2.put(91L, new RouteTripSpec(91L, //
				GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, UNIVERSITY_OF_WATERLOO_SHORT, // Waterloo
				GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, CHARLES_TERMINAL) // Kitchener
				.addTripSort(GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2551", // Charles Terminal
								"1910", // ++
								"2519", // U.W. - B.C. Matthews Hall
						})) //
				.addTripSort(GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"2519", // U.W. - B.C. Matthews Hall
								"2526", // ++
								"2551", // Charles Terminal
						})) //
				.compileBothTripSort());
		map2.put(92L, new RouteTripSpec(92L, //
				GrandRiverTransitCommons.CLOCKWISE_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "CW", //
				GrandRiverTransitCommons.COUNTERCLOCKWISE_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "CCW") //
				.addTripSort(GrandRiverTransitCommons.CLOCKWISE_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"3891", // Fischer-Hallman / Highland
								"1992", // ++
								"1972", // Fischer-Hallman / Erb
								"3465", // ++
								"3773", // ++
								"1970", // ++
								"1971", // Erb / Churchill {43.453374,-80.551137}
								"1972", // Fischer-Hallman / Erb
						})) //
				.addTripSort(GrandRiverTransitCommons.COUNTERCLOCKWISE_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1994", // Erb / Fischer-Hallman
								"4053", // ++
								"1090", // University / Seagram
								"2783", // ==
								"1167", // != Laurier => END
								"3620", // != => CONTINUE
								"1094", // ++
								"3162", // ==
								"3590", // != Fischer-Hallman / Erb =>
								"1994", // != Erb / Fischer-Hallman =>
						})) //
				.compileBothTripSort());
		map2.put(202L, new RouteTripSpec(202L, //
				0, MTrip.HEADSIGN_TYPE_STRING, THE_BOARDWALK_STATION, //
				1, MTrip.HEADSIGN_TYPE_STRING, CONESTOGA_STATION) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"1127", // Conestoga Station
								"2670", // ==
								"1166", // !=
								"3619", // ==
								"4001", // The Boardwalk Station =>
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"4001", // The Boardwalk Station <=
								"4053", // ==
								"2780", // !=
								"1090", // ==
								"3620", // ==
								"2785", // !=
								"2787", // ==
								"3799", // Conestoga Station
						})) //
				.compileBothTripSort());
		map2.put(901L, new RouteTripSpec(901L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Freeport", //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Trinity") //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"7063", // Trinity Village
								"7062", // <>
								"7060", // <>
								"1046", "3495", // <> Fairway Station
								"7064", // Grand River Hospital - Freeport Campus
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"7064", // Grand River Hospital - Freeport Campus
								"7061", // ++
								"1046", "3495", // <> Fairway Station
								"7060", // <>
								"7062", // <>
								"7063", // Trinity Village
						})) //
				.compileBothTripSort());
		map2.put(902L, new RouteTripSpec(902L, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Residences", // "East", //
				GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, "Hespeler Vlg") // "West") //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1250", // <> Queen / Guelph
								"1029", // xx <> Cooper / Queen
								"1299", // <> Tannery / Queen
								"1029", // xx <> Cooper / Queen
								"3540", // <> Queen / Adam
								"5002", // <> Queen / Tannery
								"7066", // <> Kribs / W. G. Johnson Centre
								"7065", // <> Mulberry / Jacob Hespeler Lodge
						})) //
				.addTripSort(GrandRiverTransitCommons.WEST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"7065", // <> Mulberry / Jacob Hespeler Lodge
								"7066", // <> Kribs / W. G. Johnson Centre
								"1250", // <> Queen / Guelph
								"1029", // xx <> Cooper / Queen
								"1299", // <> Tannery / Queen
								"3540", // <> Queen / Adam
								"5002", // <> Queen / Tannery
								"3540", // <> Queen / Adam
						})) //
				.compileBothTripSort());
		map2.put(8881L, new RouteTripSpec(8881L, //
				0, MTrip.HEADSIGN_TYPE_STRING, BLOCK_LINE_STATION, //
				1, MTrip.HEADSIGN_TYPE_STRING, "Lot 42" + "-" + "Oktoberfesthaus") //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"8008", // Lot 42 - Oktoberfesthaus
								"1463", // Block Line Station
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"1463", // Block Line Station
								"8008", // Lot 42 - Oktoberfesthaus
						})) //
				.compileBothTripSort());
		map2.put(9903L, new RouteTripSpec(9903L, //
				GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, UNIVERSITY_OF_WATERLOO_SHORT, //
				GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, ST_MARY_S) //
				.addTripSort(GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1143", //
								"1150", //
								"1124", //
						})) //
				.addTripSort(GrandRiverTransitCommons.SOUTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"3228", //
								"3474", //
						})) //
				.compileBothTripSort());
		map2.put(9951L, new RouteTripSpec(9951L, //
				GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, FOREST_GLEN, //
				GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, MTrip.HEADSIGN_TYPE_STRING, FAIRWAY_STATION) //
				.addTripSort(GrandRiverTransitCommons.NORTH_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"1840", // Huron Heights S.S.
								"1771", // Forest Glen
						})) //
				.addTripSort(GrandRiverTransitCommons.EAST_SPLITTED_CIRCLE, //
						Arrays.asList(new String[] { //
						"3474", // St. Marys H.S.
								"1554", // Fairview Park
						})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public String cleanStopOriginalId(String gStopId) {
		gStopId = CleanUtils.cleanMergedID(gStopId);
		return gStopId;
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
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
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		String gTripHeadsign = gTrip.getTripHeadsign();
		if (StringUtils.isEmpty(gTripHeadsign)) {
			String routeLongName = mRoute.getLongName();
			gTripHeadsign = routeLongName;
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTripHeadsign), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 2L) {
			if (Arrays.asList( //
					WESTHEIGHTS + _SLASH_ + DRIFTWOOD, // <>
					WESTHEIGHTS // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(WESTHEIGHTS, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					WESTHEIGHTS + _SLASH_ + DRIFTWOOD, // <>
					WESTHEIGHTS, // <>
					STANLEY_PARK, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 3L) {
			if (Arrays.asList( //
					CHANDLER, //
					SUNRISE_CTR_STATION, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					FREDERICK_STATION, //
					FOREST_GLEN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FOREST_GLEN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 4L) {
			if (Arrays.asList( //
					THE_BOARDWALK_STATION, // <>
					WESTMOUNT, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					FREDERICK_STATION, //
					THE_BOARDWALK_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(THE_BOARDWALK_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 5L) {
			if (Arrays.asList( //
					WATERLOO_PUBLIC_SQUARE_STATION, //
					DANIEL + _SLASH_ + BLOOMINGDALE, //
					UPTOWN, //
					BRIDGEPORT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(BRIDGEPORT, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 6L) {
			if (Arrays.asList( //
					CENTRAL_STATION, //
					CONESTOGA_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					FAIRWAY_STATION, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 7L) {
			if (Arrays.asList( //
					CHARLES_TERMINAL, // <>
					"C - " + CONESTOGA_STATION, //
					"D - " + UNIVERSITY_OF_WATERLOO_SHORT, // via University
					"E - " + UNIVERSITY_OF_WATERLOO_SHORT, // via Columbia
					UNIVERSITY + _SLASH_ + KING, //
					CONESTOGA_STATION, //
					UNIVERSITY_OF_WATERLOO_SHORT + _SLASH_ + CONESTOGA_STATION // ++
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UNIVERSITY_OF_WATERLOO_SHORT + _SLASH_ + CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CHARLES_TERMINAL, // <>
					"A - " + FAIRWAY_STATION, // via Connaught
					"B - " + FAIRWAY_STATION, // via Weber
					"F - " + FAIRWAY_STATION, // via Wilson
					FREDERICK_STATION, //
					WATERLOO_PUBLIC_SQUARE_STATION, //
					FAIRWAY_STATION // ++
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 8L) {
			if (Arrays.asList( //
					CHARLES_TERMINAL, // <>
					FAIRWAY_STATION // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					CHARLES_TERMINAL, // <>
					FAIRWAY_STATION, // <>
					UNIVERSITY + _SLASH_ + KING //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UNIVERSITY + _SLASH_ + KING, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CHARLES_TERMINAL, // <>
					FAIRWAY_STATION, // <>
					CENTRAL_STATION, //
					WESTMOUNT + _SLASH_ + UNION //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					CENTRAL_STATION, //
					UNIVERSITY + _SLASH_ + KING //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UNIVERSITY + _SLASH_ + KING, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 9L) {
			if (Arrays.asList( //
					UNIVERSITY + _SLASH_ + KING, // <>
					LAURIER, //
					UNIVERSITY_OF_WATERLOO_STATION_SHORT, //
					UNIVERSITY_OF_WATERLOO_SHORT //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UNIVERSITY_OF_WATERLOO_SHORT, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					UNIVERSITY + _SLASH_ + KING, // <>
					CEDARBRAE + _SLASH_ + GLEN_FORREST, //
					NORTHFIELD + _SLASH_ + WEBER, //
					MC_CORMICK, //
					CONESTOGA_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 10L) {
			if (Arrays.asList( //
					CONESTOGA_COLLEGE, // <>
					"A-" + FAIRWAY_STATION, //
					FAIRWAY_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"A-" + CONESTOGA_COLLEGE, //
					CONESTOGA_COLLEGE // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 12L) {
			if (Arrays.asList( //
					UNIVERSITY + _SLASH_ + KING, //
					FOREST_GLEN, //
					CONESTOGA_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					FOREST_GLEN, //
					WESTMOUNT + _SLASH_ + OTTAWA, //
					FAIRWAY_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 13L) {
			if (Arrays.asList( //
					LAURELWOOD + _SLASH_ + ERBSVILLE, //
					THE_BOARDWALK_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(THE_BOARDWALK_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 14L) { // TODO split?
			if (Arrays.asList( //
					KUMPF, //
					WATERLOO_INDUSTRIAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(WATERLOO_INDUSTRIAL, mTrip.getHeadsignId()); // TODO really?
				return true;
			}
		} else if (mTrip.getRouteId() == 16L) {
			if (Arrays.asList( //
					WATERLOO_PUBLIC_SQUARE_STATION, //
					CONESTOGA_COLLEGE // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					OTTAWA + _SLASH_ + STRASBURG, // <>
					CONESTOGA_COLLEGE, // <>
					FOREST_GLEN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FOREST_GLEN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 19L) {
			if (Arrays.asList( //
					"Parkside" + _SLASH_ + NORTHFIELD, //
					NORTHFIELD_STATION, //
					"B-" + NORTHFIELD_STATION, //
					ST_JACOBS_MARKET, //
					"A-" + ST_JACOBS_MARKET, //
					NORTHFIELD_STATION + _SLASH_ + ST_JACOBS_MARKET // ++
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(NORTHFIELD_STATION + _SLASH_ + ST_JACOBS_MARKET, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 21L) {
			if (Arrays.asList( //
					ST_JACOBS_MARKET, //
					ARTHUR + _SLASH_ + CHURCH //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(ARTHUR + _SLASH_ + CHURCH, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 22L) {
			if (Arrays.asList( //
					BLOCK_LINE_STATION, //
					FOREST_GLEN, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					SUNRISE_CTR_STATION, //
					HIGHLAND_HLS //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(HIGHLAND_HLS, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 26L) {
			if (Arrays.asList( //
					BLOCK_LINE_STATION, //
					FOREST_GLEN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FOREST_GLEN, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					GROFF, //
					TRILLIUM + _SLASH_ + WASHBURN //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(TRILLIUM + _SLASH_ + WASHBURN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 29L) {
			if (Arrays.asList( //
					CONESTOGA_STATION, //
					UNIVERSITY + _SLASH_ + KING //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UNIVERSITY + _SLASH_ + KING, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					UNIVERSITY + _SLASH_ + UNIVERSITY_OF_WATERLOO_SHORT, //
					THE_BOARDWALK_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(THE_BOARDWALK_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 31L) {
			if (Arrays.asList( //
					UNIVERSITY_OF_WATERLOO_SHORT + " Sta", //
					"Columbia" + _SLASH_ + "Sundew" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Columbia" + _SLASH_ + "Sundew", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 33L) {
			if (Arrays.asList( //
					HURON, // ==
					TRILLIUM + _SLASH_ + GROFF, //
					VILLAGE, //
					HURON_VILLAGE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(HURON_VILLAGE, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					HURON, // ==
					BLOCK_LINE_STATION, //
					FOREST_GLEN //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FOREST_GLEN, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 34L) {
			if (Arrays.asList( //
					"Victoria" + _SLASH_ + "Lackner", //
					"Shirley" + _SLASH_ + "Victoria" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Shirley" + _SLASH_ + "Victoria", mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					CENTRAL_STATION, //
					CHARLES_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CHARLES_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 51L) {
			if (Arrays.asList( //
					"A - " + FISHER_MILLS, //
					"A-" + FISHER_MILLS, //
					"A-" + GUELPH + _SLASH_ + FISHER_MILLS, //
					"B - " + MELRAN, //
					"B-" + MELRAN, //
					"B-" + "Jamieson" + _SLASH_ + "Cooper", //
					CAMBRIDGE_CENTRE_STATION, //
					FISHER_MILLS + _SLASH_ + MELRAN //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FISHER_MILLS + _SLASH_ + MELRAN, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					CAMBRIDGE_CENTRE, //
					CAMBRIDGE_CENTRE_STATION, //
					HOLIDAY_INN_DRIVE, //
					GROH + _SLASH_ + HOLIDAY_INN, //
					AINSLIE_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(AINSLIE_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 52L) {
			if (Arrays.asList( //
					FAIRWAY_STATION, // <>
					AINSLIE_TERMINAL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(AINSLIE_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 55L) {
			if (Arrays.asList( //
					"St Andrews", //
					"Cedar" + _SLASH_ + "Grand Rdg" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Cedar" + _SLASH_ + "Grand Rdg", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 57L) {
			if (Arrays.asList( //
					"Blair", //
					CONESTOGA_COLLEGE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 61L) {
			if (Arrays.asList( //
					"Shantz Hl" + _SLASH_ + "Preston Pkwy", // <>
					"Preston Pkwy" + _SLASH_ + "Fountain", //
					CONESTOGA_COLLEGE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Shantz Hl" + _SLASH_ + "Preston Pkwy", // <>
					CAMBRIDGE_CENTRE_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CAMBRIDGE_CENTRE_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 67L) {
			if (Arrays.asList( //
					"Special", //
					"Lovell Ind" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Lovell Ind", mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Lovell Ind", //
					"Sportsworld Sta" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Sportsworld Sta", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 72L) {
			if (Arrays.asList( //
					"Toyota", //
					"Cherry Blossom", //
					"Loblaws Distribution Ctr" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Loblaws Distribution Ctr", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 77L) {
			if (Arrays.asList( //
					THE_BOARDWALK, //
					THE_BOARDWALK_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(THE_BOARDWALK_STATION, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 200L) {
			if (Arrays.asList( //
					AINSLIE_TERMINAL, // <>
					UNIVERSITY_OF_WATERLOO_SHORT, //
					CONESTOGA_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					FAIRWAY_STATION, //
					AINSLIE_TERMINAL // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(AINSLIE_TERMINAL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 201L) {
			if (Arrays.asList( //
					FOREST_GLEN, //
					BLOCK_LINE_STATION, //
					BLOCK_LINE + _SLASH_ + STRASBURG, //
					CONESTOGA_STATION // <>
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					CONESTOGA_STATION, // <>
					BLOCK_LINE + _SLASH_ + STRASBURG, //
					CONESTOGA_COLLEGE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 203L) {
			if (Arrays.asList( //
					CAMBRIDGE_CENTRE, // <>
					SPORTSWORLD, //
					SPORTSWORLD_STATION, //
					CONESTOGA_COLLEGE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(CONESTOGA_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 205L) {
			if (Arrays.asList( //
					OTTAWA + _SLASH_ + RIVER, //
					SUNRISE_CTR_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SUNRISE_CTR_STATION, mTrip.getHeadsignId());
				return true;
			}
		}
		if (mTrip.getRouteId() == 206L) {
			if (Arrays.asList( //
					AINSLIE_TERMINAL, // <>
					FAIRWAY_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					AINSLIE_TERMINAL, // <>
					SOUTHWOOD + _SLASH_ + CEDAR //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SOUTHWOOD + _SLASH_ + CEDAR, mTrip.getHeadsignId());
				return true;
			}
		}
		if (mTrip.getRouteId() == 302L) { // ION Bus
			if (Arrays.asList( //
					"ION Bus", //
					FAIRWAY_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
		}
		if (mTrip.getRouteId() == 9941L) {
			if (Arrays.asList( //
					EXAM, //
					HURON_HEIGHTS //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(HURON_HEIGHTS, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					CHARLES_TERMINAL, //
					FAIRWAY_STATION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(FAIRWAY_STATION, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnepected trips to merge %s & %s\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern BUS_PLUS = Pattern.compile("( bus plus$)", Pattern.CASE_INSENSITIVE);
	private static final String BUS_PLUS_REPLACEMENT = " BusPlus";

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^[\\d]+[\\s]*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IXPRESS = Pattern.compile("(^IXpress )", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("( express$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_BUSPLUS = Pattern.compile("( busplus$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_SPECIAL = Pattern.compile("( special$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern INDUSTRIAL = Pattern.compile("(industrial|indsutrial)", Pattern.CASE_INSENSITIVE);
	private static final String INDUSTRIAL_REPLACEMENT = INDUSTRIAL_SHORT;

	private static final Pattern SECONDARY_SCHOOL_ = Pattern.compile("((^|\\W){1}(secondary school)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String SECONDARY_SCHOOL_REPLACEMENT = "$2" + SECONDARY_SCHOOL_SHORT + "$4";

	private static final Pattern UNIVERSITY_ = Pattern.compile("((^|\\W){1}(univeristy)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITY_REPLACEMENT = "$2" + "University" + "$4";

	private static final Pattern UNIVERSITY_OF_WATERLOO_ = Pattern.compile("((^|\\W){1}(uw|u\\.w\\.|university of waterloo)(\\W|$){1})",
			Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITY_OF_WATERLOO_SHORT_REPLACEMENT = "$2" + UNIVERSITY_OF_WATERLOO_SHORT + "$4";

	private static final Pattern WLU = Pattern.compile("((^|\\W){1}(wlu)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String WLU_REPLACEMENT = "$2WLU$4";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		if (Utils.isUppercaseOnly(tripHeadsign, true, true)) {
			tripHeadsign = tripHeadsign.toLowerCase(Locale.ENGLISH);
		}
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.keepTo(tripHeadsign);
		tripHeadsign = CleanUtils.removeVia(tripHeadsign);
		tripHeadsign = STARTS_WITH_IXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = BUS_PLUS.matcher(tripHeadsign).replaceAll(BUS_PLUS_REPLACEMENT);
		tripHeadsign = SECONDARY_SCHOOL_.matcher(tripHeadsign).replaceAll(SECONDARY_SCHOOL_REPLACEMENT);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_BUSPLUS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_SPECIAL.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = INDUSTRIAL.matcher(tripHeadsign).replaceAll(INDUSTRIAL_REPLACEMENT);
		tripHeadsign = UNIVERSITY_.matcher(tripHeadsign).replaceAll(UNIVERSITY_REPLACEMENT);
		tripHeadsign = UNIVERSITY_OF_WATERLOO_.matcher(tripHeadsign).replaceAll(UNIVERSITY_OF_WATERLOO_SHORT_REPLACEMENT);
		tripHeadsign = WLU.matcher(tripHeadsign).replaceAll(WLU_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = gStopName.toLowerCase(Locale.ENGLISH);
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
