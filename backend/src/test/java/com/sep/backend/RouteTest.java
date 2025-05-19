package com.sep.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import com.sep.backend.entity.RouteEntity;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.route.response.*;
import com.sep.backend.route.jsonimporter.*;
import com.sep.backend.route.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;

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

//    String basePath = "";

    @Test
    public void databaseReadTest() {
        RouteEntity routeEntity = routeRepository.save(new RouteEntity());

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
        assertEquals(idTest, waypointEntity.getRouteId());
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

    @Test
    public void serviceAllMidpointsTest() {
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
        waypointEntity2.setType(WaypointType.MID);
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

        List<WaypointResponse> waypointResponses = routeService.getMidpointsById(routeEntity.getId());

        assertEquals(2, waypointResponses.size());

        assertEquals(waypointEntity2.getLongitude(), waypointResponses.get(0).getLongitude());
        assertEquals(waypointEntity2.getLatitude(), waypointResponses.get(0).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses.get(0).getType());
        assertEquals(waypointEntity2.getIndex(), waypointResponses.get(0).getIndex());

        assertEquals(waypointEntity4.getLongitude(), waypointResponses.get(1).getLongitude());
        assertEquals(waypointEntity4.getLatitude(), waypointResponses.get(1).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses.get(1).getType());
        assertEquals(waypointEntity4.getIndex(), waypointResponses.get(1).getIndex());
    }

    @Test
    public void serviceFullRouteTest() {
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
        waypointEntity2.setType(WaypointType.MID);
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

        List<WaypointResponse> waypointResponses = routeService.getFullRouteById(routeEntity.getId());

        assertEquals(5, waypointResponses.size());

        assertEquals(waypointEntity1.getLongitude(), waypointResponses.get(0).getLongitude());
        assertEquals(waypointEntity1.getLatitude(), waypointResponses.get(0).getLatitude());
        assertEquals(WaypointType.START, waypointResponses.get(0).getType());
        assertEquals(waypointEntity1.getIndex(), waypointResponses.get(0).getIndex());

        assertEquals(waypointEntity2.getLongitude(), waypointResponses.get(1).getLongitude());
        assertEquals(waypointEntity2.getLatitude(), waypointResponses.get(1).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses.get(1).getType());
        assertEquals(waypointEntity2.getIndex(), waypointResponses.get(1).getIndex());

        assertEquals(waypointEntity3.getLongitude(), waypointResponses.get(2).getLongitude());
        assertEquals(waypointEntity3.getLatitude(), waypointResponses.get(2).getLatitude());
        assertEquals(WaypointType.POINT, waypointResponses.get(2).getType());
        assertEquals(waypointEntity3.getIndex(), waypointResponses.get(2).getIndex());

        assertEquals(waypointEntity4.getLongitude(), waypointResponses.get(3).getLongitude());
        assertEquals(waypointEntity4.getLatitude(), waypointResponses.get(3).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses.get(3).getType());
        assertEquals(waypointEntity4.getIndex(), waypointResponses.get(3).getIndex());

        assertEquals(waypointEntity5.getLongitude(), waypointResponses.get(4).getLongitude());
        assertEquals(waypointEntity5.getLatitude(), waypointResponses.get(4).getLatitude());
        assertEquals(WaypointType.END, waypointResponses.get(4).getType());
        assertEquals(waypointEntity5.getIndex(), waypointResponses.get(4).getIndex());

        RouteEntity routeEntity2 = routeRepository.save(new RouteEntity());

        WaypointEntity waypointEntity11 = new WaypointEntity();
        waypointEntity11.setRouteId(routeEntity2.getId());
        waypointEntity11.setIndex(0L);
        waypointEntity11.setLongitude("5.3267886273");
        waypointEntity11.setLatitude("1.886273");
        waypointEntity11.setType(WaypointType.START);
        waypointRepository.save(waypointEntity11);

        WaypointEntity waypointEntity12 = new WaypointEntity();
        waypointEntity12.setRouteId(routeEntity2.getId());
        waypointEntity12.setIndex(1L);
        waypointEntity12.setLongitude("6.3267886273");
        waypointEntity12.setLatitude("2.886273");
        waypointEntity12.setType(WaypointType.MID);
        waypointRepository.save(waypointEntity12);

        WaypointEntity waypointEntity13 = new WaypointEntity();
        waypointEntity13.setRouteId(routeEntity2.getId());
        waypointEntity13.setIndex(2L);
        waypointEntity13.setLongitude("7.3267886273");
        waypointEntity13.setLatitude("3.886273");
        waypointEntity13.setType(WaypointType.POINT);
        waypointRepository.save(waypointEntity13);

        WaypointEntity waypointEntity14 = new WaypointEntity();
        waypointEntity14.setRouteId(routeEntity2.getId());
        waypointEntity14.setIndex(3L);
        waypointEntity14.setLongitude("8.3267886273");
        waypointEntity14.setLatitude("4.886273");
        waypointEntity14.setType(WaypointType.MID);
        waypointRepository.save(waypointEntity14);

        WaypointEntity waypointEntity15 = new WaypointEntity();
        waypointEntity15.setRouteId(routeEntity2.getId());
        waypointEntity15.setIndex(4L);
        waypointEntity15.setLongitude("9.3267886273");
        waypointEntity15.setLatitude("5.886273");
        waypointEntity15.setType(WaypointType.POINT);
        waypointRepository.save(waypointEntity15);

        WaypointEntity waypointEntity16 = new WaypointEntity();
        waypointEntity16.setRouteId(routeEntity2.getId());
        waypointEntity16.setIndex(5L);
        waypointEntity16.setLongitude("10.3267886273");
        waypointEntity16.setLatitude("6.3267886273");
        waypointEntity16.setType(WaypointType.END);
        waypointRepository.save(waypointEntity16);

        List<WaypointResponse> waypointResponses2 = routeService.getFullRouteById(routeEntity2.getId());

        assertEquals(6, waypointResponses2.size());

        assertEquals(waypointEntity11.getLongitude(), waypointResponses2.get(0).getLongitude());
        assertEquals(waypointEntity11.getLatitude(), waypointResponses2.get(0).getLatitude());
        assertEquals(WaypointType.START, waypointResponses2.get(0).getType());
        assertEquals(waypointEntity11.getIndex(), waypointResponses2.get(0).getIndex());

        assertEquals(waypointEntity12.getLongitude(), waypointResponses2.get(1).getLongitude());
        assertEquals(waypointEntity12.getLatitude(), waypointResponses2.get(1).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses2.get(1).getType());
        assertEquals(waypointEntity12.getIndex(), waypointResponses2.get(1).getIndex());

        assertEquals(waypointEntity13.getLongitude(), waypointResponses2.get(2).getLongitude());
        assertEquals(waypointEntity13.getLatitude(), waypointResponses2.get(2).getLatitude());
        assertEquals(WaypointType.POINT, waypointResponses2.get(2).getType());
        assertEquals(waypointEntity13.getIndex(), waypointResponses2.get(2).getIndex());

        assertEquals(waypointEntity14.getLongitude(), waypointResponses2.get(3).getLongitude());
        assertEquals(waypointEntity14.getLatitude(), waypointResponses2.get(3).getLatitude());
        assertEquals(WaypointType.MID, waypointResponses2.get(3).getType());
        assertEquals(waypointEntity14.getIndex(), waypointResponses2.get(3).getIndex());

        assertEquals(waypointEntity15.getLongitude(), waypointResponses2.get(4).getLongitude());
        assertEquals(waypointEntity15.getLatitude(), waypointResponses2.get(4).getLatitude());
        assertEquals(WaypointType.POINT, waypointResponses2.get(4).getType());
        assertEquals(waypointEntity15.getIndex(), waypointResponses2.get(4).getIndex());

        assertEquals(waypointEntity16.getLongitude(), waypointResponses2.get(5).getLongitude());
        assertEquals(waypointEntity16.getLatitude(), waypointResponses2.get(5).getLatitude());
        assertEquals(WaypointType.END, waypointResponses2.get(5).getType());
        assertEquals(waypointEntity16.getIndex(), waypointResponses2.get(5).getIndex());
    }

//    @Test
//    public void serviceImportGeojsonTest() throws IOException {
//        String importResult = routeService.importGeoJson(new MockMultipartFile("file", basePath + "route_1_mhrrz_mhsm.json", "application/json", Files.readAllBytes(Path.of(basePath + "route_1_mhrrz_mhsm.json"))));
//        assertNotEquals("Unsupported Content Type", importResult);
//        assertNotEquals("failed to import route", importResult);
//        assertNotEquals("", importResult);
//    }
//
//    @Test
//    public void importGeoJsonTest() throws IOException {
//        String route1Id = RouteImport.importRoute(Files.readString(Path.of(basePath + "route_1_mhrrz_mhsm.json")),routeRepository,waypointRepository);
//        String route2Id = RouteImport.importRoute(Files.readString(Path.of(basePath + "route_2_unie_mhhrw_unidu.json")),routeRepository,waypointRepository);
//        String route3Id = RouteImport.importRoute(Files.readString(Path.of(basePath + "route_3_dhbf_duhbf_mhhbf_ehbf.json")),routeRepository,waypointRepository);
//        String route4Id = RouteImport.importRoute(Files.readString(Path.of(basePath + "route_4_mhhei.json")),routeRepository,waypointRepository);
//        String route5Id = RouteImport.importRoute(Files.readString(Path.of(basePath + "route_5_mhhei_no_midpoint.json")),routeRepository,waypointRepository);
//
//        assertEquals(WaypointType.START, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route2Id),0L).orElseThrow().getType());
//        assertEquals(WaypointType.POINT, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route2Id),1L).orElseThrow().getType());
//        assertEquals(WaypointType.POINT, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route2Id),431L).orElseThrow().getType());
//        assertEquals(WaypointType.END, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route2Id),432L).orElseThrow().getType());
//        assertEquals(1L, waypointRepository.countByRouteIdAndType(Long.valueOf(route2Id), WaypointType.MID));
//        assertEquals(430L, waypointRepository.countByRouteIdAndType(Long.valueOf(route2Id), WaypointType.POINT));
//
//        assertEquals(2, waypointRepository.countByRouteIdAndType(Long.valueOf(route3Id), WaypointType.MID));
//        assertEquals("6.793248", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route3Id),0L).orElseThrow().getLongitude());
//        assertEquals("51.221014", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route3Id),0L).orElseThrow().getLatitude());
//        assertEquals("7.0131", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route3Id),771L).orElseThrow().getLongitude());
//        assertEquals("51.450727", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route3Id),771L).orElseThrow().getLatitude());
//        assertEquals(2L, routeService.getRouteById(Long.valueOf(route3Id)).getMidpointCount());
//        assertEquals(768L, waypointRepository.countByRouteIdAndType(Long.valueOf(route3Id), WaypointType.POINT));
//
//        assertEquals("6.924997", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route4Id),2L).orElseThrow().getLongitude());
//        assertEquals("51.433167", waypointRepository.findByRouteIdAndIndex(Long.valueOf(route4Id),2L).orElseThrow().getLatitude());
//        assertEquals(WaypointType.MID, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route4Id),1L).orElseThrow().getType());
//
//        assertEquals(1L, waypointRepository.countByRouteIdAndType(Long.valueOf(route5Id),WaypointType.START));
//        assertEquals(1L, waypointRepository.countByRouteIdAndType(Long.valueOf(route5Id),WaypointType.POINT));
//        assertEquals(1L, waypointRepository.countByRouteIdAndType(Long.valueOf(route5Id),WaypointType.END));
//        assertEquals(WaypointType.START, waypointRepository.findByRouteIdAndIndex(Long.valueOf(route5Id),0L).orElseThrow().getType());
//        assertEquals(0L, routeService.getRouteById(Long.valueOf(route5Id)).getMidpointCount());
//    }
}
