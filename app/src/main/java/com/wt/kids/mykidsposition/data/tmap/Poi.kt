package com.wt.kids.mykidsposition.data.tmap

data class Poi(
    //POI 의  id
    val id: String = "",

    //POI 의 name
    val name: String = "",

    //POI 에 대한 전화번호
    val telNo: String = "",

    //시설물 입구 위도 좌표
    val frontLat: Float = 0.0f,

    //시설물 입구 경도 좌표
    val frontLon: Float = 0.0f,

    //중심점 위도 좌표
    val noorLat: Float = 0.0f,

    //중심점 경도 좌표
    val noorLon: Float = 0.0f,

    //표출 주소 대분류명
    val upperAddrName: String = "",

    //표출 주소 중분류명
    val middleAddrName: String = "",

    //표출 주소 소분류명
    val lowerAddrName: String = "",

    //표출 주소 세분류명
    val detailAddrName: String = "",

    //본번
    val firstNo: String = "",

    //부번
    val secondNo: String = "",

    //도로명
    val roadName: String = "",

    //건물번호 1
    val firstBuildNo: String = "",

    //건물번호 2
    val secondBuildNo: String = "",

    //업종 대분류명
    val mlClass: String = "",

    //거리(km)
    val radius: String = "",

    //업소명
    val bizName: String = "",

    //시설목적
    val upperBizName: String = "",

    //시설분류
    val middleBizName: String = "",

    //시설이름 ex) 지하철역 병원 등
    val lowerBizName: String = "",

    //상세 이름
    val detailBizName: String = "",

    //길안내 요청 유무
    val rpFlag: String = "",

    //주차 가능유무
    val parkFlag: String = "",

    //POI 상세정보 유무
    val detailInfoFlag: String = "",

    //소개 정보
    val desc: String = ""
)
