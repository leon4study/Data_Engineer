package com.fastcampus.de.java.clip11;

import java.time.*;

public class ZonedDateTimeExample {
    public static void main(String[] args) {
        //ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        //ZoneId zoneId2 = ZoneId.of("Asia/Merong"); // Unknown time-zone ID: Asia/Merong
        System.out.println(zoneId);
        //System.out.println(ZoneId.getAvailableZoneIds());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        System.out.println(zonedDateTime);

        System.out.println(ZonedDateTime.parse("2025-04-08T14:32:03.743+02:00[Europe/Paris]"));

        ZoneOffset offset = ZoneOffset.of("+02:00");
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, offset );
        System.out.println(offsetDateTime);
    }
}
