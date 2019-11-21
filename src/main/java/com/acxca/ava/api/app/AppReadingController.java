package com.acxca.ava.api.app;

import com.acxca.ava.entity.BookMark;
import com.acxca.ava.repository.ReadingRepository;
import com.acxca.components.spring.jwt.JwtUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "app/reading",produces= MediaType.APPLICATION_JSON_VALUE)
public class AppReadingController {
    @Autowired
    private ReadingRepository readingRepository;

    @RequestMapping(path = "bookmark/list",method = RequestMethod.GET)
    public ResponseEntity<Object> searchBookMark() {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<BookMark> bookMarks = readingRepository.selectBookMarks(ud.getId());

        return new ResponseEntity(bookMarks, HttpStatus.OK);
    }

    @RequestMapping(path = "bookmark",method = RequestMethod.POST)
    public ResponseEntity<Object> addBookMark(@RequestBody BookMark bookMark) {
        JwtUserDetail ud = (JwtUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookMark.setUser_id(ud.getId());
        bookMark.setId(UUID.randomUUID().toString());

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
