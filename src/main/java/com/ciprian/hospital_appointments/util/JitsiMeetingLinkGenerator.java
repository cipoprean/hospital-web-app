package com.ciprian.hospital_appointments.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@UtilityClass
@Slf4j
public class JitsiMeetingLinkGenerator {

    private final String ROOM_NAME_PREFIX = "mdh-";
    private final String JITSI_LINK_PREFIX = "https://jit.si/";

    public String generateJitsiMeetingLink() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String jitsiRoomName = ROOM_NAME_PREFIX + uuid.substring(0, 10);
        String meetingLink = JITSI_LINK_PREFIX + jitsiRoomName;

        log.info("Generated Jitsi Meeting Link: {}", meetingLink);

        return meetingLink;
    }
}
