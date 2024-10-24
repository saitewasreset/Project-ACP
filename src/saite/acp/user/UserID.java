package saite.acp.user;

import saite.acp.command.CommandException;
import saite.acp.command.IllegalArgumentContentException;
import saite.acp.util.Range;

public class UserID {
    private final String rawID;

    private static final Range<Integer> undergraduateYearRange = new Range<Integer>(19, 24);
    private static final Range<Integer> undergraduateSchoolIDRange = new Range<Integer>(1, 43);
    private static final Range<Integer> undergraduateClassIDRange = new Range<Integer>(1, 6);
    private static final Range<Integer> undergraduateRecordIDRange = new Range<Integer>(1, 999);

    private static final String[] masterPrefixList = {"SY", "ZY"};
    private static final Range<Integer> masterYearRange = new Range<Integer>(21, 24);
    private static final Range<Integer> masterSchoolIDRange = new Range<Integer>(1, 43);
    private static final Range<Integer> masterClassIDRange = new Range<Integer>(1, 6);
    private static final Range<Integer> masterRecordIDRange = new Range<Integer>(1, 99);

    private static final String[] doctoralPrefixList = {"BY"};
    private static final Range<Integer> doctoralYearRange = new Range<Integer>(19, 24);
    private static final Range<Integer> doctoralSchoolIDRange = new Range<Integer>(1, 43);
    private static final Range<Integer> doctoralClassIDRange = new Range<Integer>(1, 6);
    private static final Range<Integer> doctoralRecordIDRange = new Range<Integer>(1, 99);

    private static final String administratorPrefix = "AD";

    public UserID(String rawID) throws CommandException {
        // check
        this.rawID = rawID;

        if (rawID.length() == 8) {
            try {
                int year = Integer.parseInt(rawID.substring(0, 2));
                int schoolID = Integer.parseInt(rawID.substring(2, 4));
                int classID = Integer.parseInt(rawID.substring(4, 5));
                int recordID = Integer.parseInt(rawID.substring(5));


                if (!(undergraduateYearRange.checkValue(year) && undergraduateSchoolIDRange.checkValue(schoolID)
                        && undergraduateClassIDRange.checkValue(classID)
                        && undergraduateRecordIDRange.checkValue(recordID))) {
                    throw new IllegalArgumentContentException("user id");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentContentException("user id");
            }
        } else if (rawID.length() == 9) {
            try {
                String type = rawID.substring(0, 2);
                int year = Integer.parseInt(rawID.substring(2, 4));
                int schoolID = Integer.parseInt(rawID.substring(4, 6));
                int classID = Integer.parseInt(rawID.substring(6, 7));
                int recordID = Integer.parseInt(rawID.substring(7));

                for (String masterPrefix : masterPrefixList) {
                    if (type.equals(masterPrefix)) {
                        if (masterYearRange.checkValue(year) && masterSchoolIDRange.checkValue(schoolID)
                                && masterClassIDRange.checkValue(classID)
                                && masterRecordIDRange.checkValue(recordID)) {
                            return;
                        } else {
                            throw new IllegalArgumentContentException("user id");
                        }
                    }
                }

                for (String doctoralPrefix : doctoralPrefixList) {
                    if (type.equals(doctoralPrefix)) {
                        if (doctoralYearRange.checkValue(year) && doctoralSchoolIDRange.checkValue(schoolID)
                                && doctoralClassIDRange.checkValue(classID)
                                && doctoralRecordIDRange.checkValue(recordID)) {
                            return;
                        } else {
                            throw new IllegalArgumentContentException("user id");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentContentException("user id");
            }

            throw new IllegalArgumentContentException("user id");
        } else if (rawID.length() == 5) {
            String prefix = rawID.substring(0, 2);

            if (prefix.equals(administratorPrefix)) {
                String integerPart = rawID.substring(2);

                try {
                    int iPart = Integer.parseInt(integerPart);

                    if (iPart == 0) {
                        throw new IllegalArgumentContentException("user id");
                    } else {
                        return;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentContentException("user id");
                }
            } else {
                try {
                    int iPart = Integer.parseInt(rawID);

                    if (iPart == 0) {
                        throw new IllegalArgumentContentException("user id");
                    } else {
                        return;
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentContentException("user id");
                }
            }

        } else {
            throw new IllegalArgumentContentException("user id");
        }


    }

    public String getRawID() {
        return rawID;
    }

    @Override
    public String toString() {
        return this.rawID;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof UserID other) {
            return other.rawID.equals(this.rawID);
        } else {
            return false;
        }


    }

    @Override
    public int hashCode() {
        return this.rawID.hashCode();
    }
}
