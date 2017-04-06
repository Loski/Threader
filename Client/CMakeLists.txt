project(Scrabble)
cmake_minimum_required(VERSION 3.2)
#set(EXECUTABLE_OUTPUT_PATH dist/${CMAKE_BUILD_TYPE})
#set(CMAKE_CXX_FLAGS "-Wall -Wextra -std=c++11")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -Wall -Wextra -g")

set(CMAKE_BUILD_TYPE debug)

#Génération de la liste des fichiers sources

find_package(PkgConfig REQUIRED)
pkg_check_modules(GTK3 REQUIRED gtk+-3.0)
# Setup CMake to use GTK+, tell the compiler where to look for headers
# and to the linker where to look for libraries
include_directories(${GTK3_INCLUDE_DIRS})
link_directories(${GTK3_LIBRARY_DIRS})
# Add other flags to the compiler
add_definitions(${GTK3_CFLAGS_OTHER})



pkg_check_modules(GLib REQUIRED glib-2.0)
# Setup CMake to use GTK+, tell the compiler where to look for headers
# and to the linker where to look for libraries
include_directories(${GLib_INCLUDE_DIRS})
link_directories(${GLib_LIBRARY_DIRS})
# Add other flags to the compiler
add_definitions(${GLib_CFLAGS_OTHER})


include_directories(include)


file(

        GLOB_RECURSE
        source_files
        src/*
	include/*
)


#Déclaration de l'exécutable

add_executable(
        client
        ${source_files}
)

set(THREADS_PREFER_PTHREAD_FLAG ON)
find_package(Threads REQUIRED)
target_link_libraries(client Threads::Threads)

target_link_libraries(client ${GTK3_LIBRARIES})
target_link_libraries(client ${GLib_LIBRARIES})

