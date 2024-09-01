package com.msgs.domain.tripschedule.service;

import com.msgs.domain.tripplace.dto.PlaceInfoDTO;
import com.msgs.domain.tripschedule.dto.PlanBlockDTO;
import java.util.List;
import java.util.Map;

public interface TripScheduleService {
    List<PlaceInfoDTO> getDormList(int areaCode, List<Integer> sigunguCodeList);
    List<PlaceInfoDTO> getPlaceList(int areaCode, List<Integer> sigunguCodeList);
    Boolean saveSchedule(List<String> dateList, Map<Integer, List<PlanBlockDTO>> planList, String cityName);

    Map<String, Object> getSchedule(int scheduleId);
    Boolean updateSchedule(List<String> dateList, Map<Integer, List<PlanBlockDTO>> planList, String scheduleId);

}
