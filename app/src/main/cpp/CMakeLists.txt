cmake_minimum_required(VERSION 3.4.1)

# 프로젝트 이름 정의
project(NativeCrashHandler)

# 네이티브 크래시 핸들러 소스 파일 추가
add_library(native-lib SHARED
        NativeCrashHandler.cpp)  # 파일 경로를 수정해야 할 수 있음

find_library(log-lib log)

# JNI 헤더 파일 위치 설정
include_directories(${CMAKE_SOURCE_DIR}/include)

# 네이티브 라이브러리와 필요한 라이브러리 연결
target_link_libraries(native-lib ${log-lib})
