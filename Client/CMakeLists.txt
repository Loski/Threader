project(Scrabble)
set(EXECUTABLE_OUTPUT_PATH dist/Debug/GNU-Linux/${CMAKE_BUILD_TYPE})

#Génération de la liste des fichiers sources


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

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -Wall")
