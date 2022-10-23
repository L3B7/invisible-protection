package hu.bpe.ipmapper;

import com.google.gson.Gson;
import hu.bpe.ipmapper.DataRepository;
import hu.bpe.ipmapper.DataStruct;
import hu.bpe.ipmapper.POJOdata;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ping")
public class PingController {
    @Autowired
    private final DataRepository dataRepository;

    @PostMapping()
    public String savePingData(@RequestBody DataStruct pingData, HttpServletRequest request) {
        POJOdata pjd = new  POJOdata(pingData,request.getRemoteAddr().toString(),0);
        dataRepository.saveAndFlush(pjd);
        return pjd.toString();

    }
}

