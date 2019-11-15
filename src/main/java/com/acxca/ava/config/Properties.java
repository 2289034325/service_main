package com.acxca.ava.config;

import com.acxca.components.spring.config.IProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties implements IProperties {
    @Value("${oss.endpoint}")
    private String ossEndpoint;
    @Value("${oss.bucket}")
    private String ossBucket;
    @Value("${oss.media.folder}")
    private String ossMediaFolder;
    @Value("${oss.key}")
    private String ossKey;
    @Value("${oss.secret}")
    private String ossSecret;

    @Value("${media.local.path}")
    private String mediaLocalPath;


    @Value("${jwt.auth.token.expired.seconds}")
    private int tokenExpiredSeconds;
    @Value("${jwt.auth.header}")
    private String authHeader;
    @Value("${jwt.auth.tokenHead}")
    private String tokenHead;
    @Value("${jwt.auth.secret}")
    private String jwtSecret;


    @Value("${learn.phase.max}")
    private int learnPhaseMax;
    @Value("${learn.phase.interval.1}")
    private int learnPhaseInterval1;
    @Value("${learn.phase.interval.2}")
    private int learnPhaseInterval2;
    @Value("${learn.phase.interval.3}")
    private int learnPhaseInterval3;
    @Value("${learn.phase.interval.4}")
    private int learnPhaseInterval4;


    @Value("${spider.service.url}")
    private String spiderServiceUrl;
    @Value("${spider.service.api.vocabulary.msg}")
    private String spiderServiceApiVocMsg;
    @Value("${spider.service.api.vocabulary.grab}")
    private String spiderServiceApiVocGrab;
    @Value("${spider.service.api.vocabulary.queue}")
    private String spiderServiceApiVocQueue;

    public String getSpiderServiceApiVocQueue() {
        return spiderServiceApiVocQueue;
    }

    public void setSpiderServiceApiVocQueue(String spiderServiceApiVocQueue) {
        this.spiderServiceApiVocQueue = spiderServiceApiVocQueue;
    }

    public String getOssEndpoint() {
        return ossEndpoint;
    }

    public void setOssEndpoint(String ossEndpoint) {
        this.ossEndpoint = ossEndpoint;
    }

    public String getOssBucket() {
        return ossBucket;
    }

    public void setOssBucket(String ossBucket) {
        this.ossBucket = ossBucket;
    }

    public String getOssMediaFolder() {
        return ossMediaFolder;
    }

    public void setOssMediaFolder(String ossMediaFolder) {
        this.ossMediaFolder = ossMediaFolder;
    }

    public String getOssKey() {
        return ossKey;
    }

    public void setOssKey(String ossKey) {
        this.ossKey = ossKey;
    }

    public String getOssSecret() {
        return ossSecret;
    }

    public void setOssSecret(String ossSecret) {
        this.ossSecret = ossSecret;
    }

    public String getMediaLocalPath() {
        return mediaLocalPath;
    }

    public void setMediaLocalPath(String mediaLocalPath) {
        this.mediaLocalPath = mediaLocalPath;
    }

    public String getSpiderServiceUrl() {
        return spiderServiceUrl;
    }

    public void setSpiderServiceUrl(String spiderServiceUrl) {
        this.spiderServiceUrl = spiderServiceUrl;
    }

    public String getSpiderServiceApiVocMsg() {
        return spiderServiceApiVocMsg;
    }

    public void setSpiderServiceApiVocMsg(String spiderServiceApiVocMsg) {
        this.spiderServiceApiVocMsg = spiderServiceApiVocMsg;
    }

    public String getSpiderServiceApiVocGrab() {
        return spiderServiceApiVocGrab;
    }

    public void setSpiderServiceApiVocGrab(String spiderServiceApiVocGrab) {
        this.spiderServiceApiVocGrab = spiderServiceApiVocGrab;
    }

    public int getLearnPhaseMax() {
        return learnPhaseMax;
    }

    public void setLearnPhaseMax(int learnPhaseMax) {
        this.learnPhaseMax = learnPhaseMax;
    }

    public int getLearnPhaseInterval1() {
        return learnPhaseInterval1;
    }

    public void setLearnPhaseInterval1(int learnPhaseInterval1) {
        this.learnPhaseInterval1 = learnPhaseInterval1;
    }

    public int getLearnPhaseInterval2() {
        return learnPhaseInterval2;
    }

    public void setLearnPhaseInterval2(int learnPhaseInterval2) {
        this.learnPhaseInterval2 = learnPhaseInterval2;
    }

    public int getLearnPhaseInterval3() {
        return learnPhaseInterval3;
    }

    public void setLearnPhaseInterval3(int learnPhaseInterval3) {
        this.learnPhaseInterval3 = learnPhaseInterval3;
    }

    public int getLearnPhaseInterval4() {
        return learnPhaseInterval4;
    }

    public void setLearnPhaseInterval4(int learnPhaseInterval4) {
        this.learnPhaseInterval4 = learnPhaseInterval4;
    }

    @Override
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public int getTokenExpiredSeconds() {
        return tokenExpiredSeconds;
    }

    public void setTokenExpiredSeconds(int tokenExpiredSeconds) {
        this.tokenExpiredSeconds = tokenExpiredSeconds;
    }

    @Override
    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    @Override
    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }
}
