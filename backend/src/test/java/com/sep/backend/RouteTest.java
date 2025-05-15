package com.sep.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.sep.backend.entity.RouteEntity;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.route.RouteRepository;
import com.sep.backend.route.WaypointRepository;
import com.sep.backend.route.RouteService;
import com.sep.backend.route.WaypointType;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
public class RouteTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private WaypointRepository waypointRepository;

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
}
