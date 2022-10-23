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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
public class FrontendApi {
    @Autowired
    private final DataRepository dataRepository;

    @GetMapping()
    public String savePingData(@RequestParam(required = false, defaultValue
            = "100") Integer limit, HttpServletRequest request) {

        Page<POJOdata> records =dataRepository.findAll(PageRequest.of(0, limit==0?1:limit, Sort.by(Sort.Direction.DESC, "id")));
        Gson gson = new Gson();

        return gson.toJson(records);
    }
}
