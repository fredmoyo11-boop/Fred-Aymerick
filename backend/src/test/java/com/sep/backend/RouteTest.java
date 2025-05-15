package com.sep.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sep.backend.entity.RouteEntity;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.route.response.*;
import com.sep.backend.route.jsonimporter.*;
import com.sep.backend.route.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class RouteTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private WaypointRepository waypointRepository;

    @Autowired
    private RouteService routeService;

    @Test
    public void databaseReadTest() {
        RouteEntity routeEntity = routeRepository.save(new RouteEntity());
        //routeRepository.flush();
        //routeEntity = routeRepository.findById(routeEntity.getId()).orElse(null);
        WaypointEntity waypointEntity = new WaypointEntity();
        waypointEntity.setRouteId(routeEntity.getId());
        waypointEntity.setIndex(0L);
        waypointEntity.setLongitude("5.3267886273");
        waypointEntity.setLatitude("1.886273");
        waypointEntity.setType(WaypointType.POINT);
        waypointRepository.save(waypointEntity);

        String LonTest = "5.3267886273";
        String LatTest = "1.886273";
        String TypeTest = "POINT";
        Long indexTest = 0L;
        Long idTest = routeEntity.getId();

        assertEquals(LonTest, waypointEntity.getLongitude());
        assertEquals(LatTest, waypointEntity.getLatitude());
        assertEquals(TypeTest, waypointEntity.getType());
        assertEquals(indexTest, waypointEntity.getIndex());
        assertEquals(idTest, waypointEntity.getId());
    }

    @Test
    public void serviceMetadataTest() {
        RouteEntity routeEntity = routeRepository.save(new RouteEntity());
        WaypointEntity waypointEntity1 = new WaypointEntity();
        waypointEntity1.setRouteId(routeEntity.getId());
        waypointEntity1.setIndex(0L);
        waypointEntity1.setLongitude("5.3267886273");
        waypointEntity1.setLatitude("1.886273");
        waypointEntity1.setType(WaypointType.START);
        waypointRepository.save(waypointEntity1);

        WaypointEntity waypointEntity2 = new WaypointEntity();
        waypointEntity2.setRouteId(routeEntity.getId());
        waypointEntity2.setIndex(1L);
        waypointEntity2.setLongitude("6.3267886273");
        waypointEntity2.setLatitude("2.886273");
        waypointEntity2.setType(WaypointType.POINT);
        waypointRepository.save(waypointEntity2);

        WaypointEntity waypointEntity3 = new WaypointEntity();
        waypointEntity3.setRouteId(routeEntity.getId());
        waypointEntity3.setIndex(2L);
        waypointEntity3.setLongitude("7.3267886273");
        waypointEntity3.setLatitude("3.886273");
        waypointEntity3.setType(WaypointType.POINT);
        waypointRepository.save(waypointEntity3);

        WaypointEntity waypointEntity4 = new WaypointEntity();
        waypointEntity4.setRouteId(routeEntity.getId());
        waypointEntity4.setIndex(3L);
        waypointEntity4.setLongitude("8.3267886273");
        waypointEntity4.setLatitude("4.886273");
        waypointEntity4.setType(WaypointType.MID);
        waypointRepository.save(waypointEntity4);

        WaypointEntity waypointEntity5 = new WaypointEntity();
        waypointEntity5.setRouteId(routeEntity.getId());
        waypointEntity5.setIndex(4L);
        waypointEntity5.setLongitude("9.3267886273");
        waypointEntity5.setLatitude("5.3267886273");
        waypointEntity5.setType(WaypointType.END);
        waypointRepository.save(waypointEntity5);

        RouteResponse routeResponse = routeService.getRouteById(routeEntity.getId());
        assertEquals(waypointEntity1.getLongitude(), routeResponse.getStartLongitude());
        assertEquals(waypointEntity1.getLatitude(), routeResponse.getStartLatitude());
        assertEquals(waypointEntity5.getLongitude(), routeResponse.getEndLongitude());
        assertEquals(waypointEntity5.getLatitude(), routeResponse.getEndLatitude());
        assertEquals(2L, routeResponse.getOtherPointCount());
        assertEquals(1L, routeResponse.getMidpointCount());
    }
}
