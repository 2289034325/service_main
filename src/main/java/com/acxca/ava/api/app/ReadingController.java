package com.acxca.ava.api.app;

import com.acxca.ava.config.Properties;
import com.acxca.ava.entity.BookMark;
import com.acxca.ava.repository.ReadingRepository;
import com.acxca.components.java.util.AliyunOSSClient;
import com.acxca.components.spring.jwt.JwtCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "reading",produces= MediaType.APPLICATION_JSON_VALUE)
public class ReadingController {
    @Autowired
    private ReadingRepository readingRepository;

    @RequestMapping(path = "bookmark/list",method = RequestMethod.GET)
    public ResponseEntity<Object> searchBookMark() {
        JwtCertificate ud = (JwtCertificate) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<BookMark> bookMarks = readingRepository.selectBookMarks(ud.getId());

        return new ResponseEntity(bookMarks, HttpStatus.OK);
    }

    @RequestMapping(path = "bookmark",method = RequestMethod.POST)
    public ResponseEntity<Object> addBookMark(@RequestBody BookMark bookMark) {
        JwtCertificate ud = (JwtCertificate) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookMark.setUser_id(ud.getId());

        readingRepository.insertBookMark(bookMark);

        return new ResponseEntity(bookMark, HttpStatus.OK);
    }

    @RequestMapping(path = "bookmark",method = RequestMethod.PUT)
    public ResponseEntity<Object> editBookMark(@RequestBody BookMark bookMark) {

        readingRepository.updateBookMark(bookMark);

        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(path = "bookmark/{id}",method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteBookMark(@PathVariable String id) {

        readingRepository.deleteBookMark(id);

        return new ResponseEntity("", HttpStatus.OK);
    }
}
