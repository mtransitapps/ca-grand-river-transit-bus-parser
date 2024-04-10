package org.mtransit.parser.ca_grand_river_transit_bus;

import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://www.grt.ca/en/about-grt/open-data.aspx
public class GrandRiverTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new GrandRiverTransitBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Grand River Transit";
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if ("Out Of Service".equalsIgnoreCase(gTrip.getTripHeadsign())) {
			return true; // exclude
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false; // used for GTFS-RT
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, routeLongName, getIgnoredWords());
		routeLongName = BUS_PLUS.matcher(routeLongName).replaceAll(BUS_PLUS_REPLACEMENT);
		routeLongName = CleanUtils.CLEAN_AND.matcher(routeLongName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "0168B3"; // BLUE (PDF SCHEDULE)

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	// https://www.grt.ca/en/schedules-maps/desktop-realtime-map.aspx
	@Override
	public @Nullable String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final int rsn = Integer.parseInt(gRoute.getRouteShortName());
		switch (rsn) {
		// @formatter:off
		case 1: return "0099CC";
		case 2: return "FFCC33";
		case 3: return "9900CC";
		case 4: return "666633";
		case 5: return "0099FF";
		case 6: return "000066";
		case 7: return "CC0000";
		case 8: return "009933";
		case 9: return "FF9933";
		case 10: return "0066CC";
		case 11: return "3366FF";
		case 12: return "CC3399";
		case 13: return "FFCC00";
		case 14: return "003333";
		case 15: return "993399";
		case 16: return "669933";
		case 17: return "666666";
		case 19: return "CC6600";
		case 20: return "CC99CC";
		case 21: return "99CC00";
		case 22: return "666699";
		case 23: return "FF99CC";
		case 24: return "333333";
		case 25: return "3399CC";
		case 26: return "D0BA00";
		case 27: return "FF9966";
		case 28: return "C4D600";
		case 29: return "993366";
		case 30: return "00C513";
		case 31: return "999966";
		case 33: return "089018";
		case 34: return "92278F";
		case 35: return null; // TODO?
		case 36: return "D0BA00";
		case 50: return "00C513";
		case 51: return "CC0000";
		case 52: return "000099";
		case 53: return "009933";
		case 54: return "0099CC";
		case 55: return "993399";
		case 56: return "D0BA00";
		case 57: return "FF9900";
		case 58: return "333300";
		case 59: return "CC0099";
		case 60: return "333366";
		case 61: return "009999";
		case 62: return "666666";
		case 63: return "FFCC00";
		case 64: return "990033";
		case 65: return null; // TODO?
		case 67: return "E09400";
		case 72: return "996666";
		case 73: return "0099CC";
		case 75: return "B72700";
		case 76: return "000066";
		case 77: return "E09400";
		case 78: return "EA4AA3";
		case 91: return "009CE0";
		case 92: return "003986";
		case 110: return "0066CC";
		case 111: return "FF3366";
		case 116: return "669933";
		case 200: return "0066FF";
		case 201: return "000000";
		case 202: return "000000";
		case 203: return "000000";
		case 204: return "000000";
		case 205: return "000000";
		case 206: return "000000";
		case 302: return "05AA64";
		case 901: return null; // TODO?
		case 902: return null; // TODO?
		case 9801: return "009CE0";
		case 9802: return null; // TODO?
		case 9841: return null; // TODO?
		case 9851: return "009CE0";
		case 9852: return "003986";
		case 9901: return "009CE0";
		case 9903: return "089018";
		case 9904: return "880091";
		case 9905: return "B72700";
		case 9922: return null; // TODO?
		case 9931: return "009CE0";
		case 9932: return "003986";
		case 9941: return null; // TODO?
		case 9942: return null; // TODO?
		case 9951: return "009CE0";
		case 9952: return "003986";
		case 9953: return "089018";
		case 9954: return "880091";
		case 9961: return "009CE0";
		case 9963: return "089018";
		case 9964: return "880091";
		case 9983: return "089018";
		// @formatter:on
		default:
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute.toStringPlus());
		}
	}

	@NotNull
	@Override
	public String cleanStopOriginalId(@NotNull String gStopId) {
		gStopId = CleanUtils.cleanMergedID(gStopId);
		return gStopId;
	}

	private static final Pattern STARTS_WITH_LETTER = Pattern.compile("(^[A-Z][- ])", Pattern.CASE_INSENSITIVE);

	@Nullable
	@Override
	public String selectDirectionHeadSign(@Nullable String headSign1, @Nullable String headSign2) {
		if (StringUtils.equals(headSign1, headSign2)) {
			return null; // can NOT select
		}
		final boolean headSign1StartsWithLetter = headSign1 != null && STARTS_WITH_LETTER.matcher(headSign1).matches();
		final boolean headSign2StartsWithLetter = headSign2 != null && STARTS_WITH_LETTER.matcher(headSign2).matches();
		if (headSign1StartsWithLetter) {
			if (!headSign2StartsWithLetter) {
				return headSign2;
			}
		} else if (headSign2StartsWithLetter) {
			return headSign1;
		}
		return null;
	}

	@NotNull
	@Override
	public String cleanDirectionHeadsign(int directionId, boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = super.cleanDirectionHeadsign(directionId, fromStopName, directionHeadSign);
		directionHeadSign = STARTS_WITH_LETTER.matcher(directionHeadSign).replaceAll(EMPTY);
		return directionHeadSign;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern BUS_PLUS = Pattern.compile("( bus plus$)", Pattern.CASE_INSENSITIVE);
	private static final String BUS_PLUS_REPLACEMENT = " BusPlus";

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^\\d+\\s*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_IXPRESS = Pattern.compile("(^IXpress )", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("( express$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_BUSPLUS = Pattern.compile("( busplus$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ENDS_WITH_SPECIAL = Pattern.compile("( special$)", Pattern.CASE_INSENSITIVE);

	private static final String INDUSTRIAL_SHORT = "Ind";
	private static final Pattern INDUSTRIAL = CleanUtils.cleanWords("indsutrial");
	private static final String INDUSTRIAL_REPLACEMENT = CleanUtils.cleanWordsReplacement(INDUSTRIAL_SHORT);

	private static final String SECONDARY_SCHOOL_SHORT = "SS";
	private static final Pattern SECONDARY_SCHOOL_ = Pattern.compile("((^|\\W)(secondary school)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String SECONDARY_SCHOOL_REPLACEMENT = "$2" + SECONDARY_SCHOOL_SHORT + "$4";

	private static final Pattern UNIVERSITY_ = Pattern.compile("((^|\\W)(univeristy)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String UNIVERSITY_REPLACEMENT = "$2" + "University" + "$4";

	private static final String UNIVERSITY_OF_WATERLOO_SHORT = "UW";
	private static final Pattern UNIVERSITY_OF_WATERLOO_ = CleanUtils.cleanWords("university of waterloo");
	private static final String UNIVERSITY_OF_WATERLOO_SHORT_REPLACEMENT = "$2" + UNIVERSITY_OF_WATERLOO_SHORT + "$4";

	private static final Pattern WLU = Pattern.compile("((^|\\W)(wlu)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String WLU_REPLACEMENT = "$2" + "WLU" + "$4";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, tripHeadsign, getIgnoredWords());
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = STARTS_WITH_IXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = BUS_PLUS.matcher(tripHeadsign).replaceAll(BUS_PLUS_REPLACEMENT);
		tripHeadsign = SECONDARY_SCHOOL_.matcher(tripHeadsign).replaceAll(SECONDARY_SCHOOL_REPLACEMENT);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_BUSPLUS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_SPECIAL.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = INDUSTRIAL.matcher(tripHeadsign).replaceAll(INDUSTRIAL_REPLACEMENT);
		tripHeadsign = UNIVERSITY_.matcher(tripHeadsign).replaceAll(UNIVERSITY_REPLACEMENT);
		tripHeadsign = UNIVERSITY_OF_WATERLOO_.matcher(tripHeadsign).replaceAll(UNIVERSITY_OF_WATERLOO_SHORT_REPLACEMENT);
		tripHeadsign = WLU.matcher(tripHeadsign).replaceAll(WLU_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"AM", "PM",
				"ION", "WLU", "UW", "SS"
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName, getIgnoredWords());
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		return String.valueOf(getStopId(gStop)); // using stop ID as stop code
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId = gStop.getStopId();
		if (!CharUtils.isDigitsOnly(stopId)) {
			final Matcher matcher = DIGITS.matcher(stopId);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group()); // used for GTFS-RT
			}
		}
		return super.getStopId(gStop);
	}
}
