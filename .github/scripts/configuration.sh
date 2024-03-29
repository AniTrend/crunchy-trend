#!/bin/bash

#
# Copyright (C) 2021  AniTrend
#
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

function create_directories {
    mkdir -p ./app-core/src/main/resources/org/koin/core/
    cd ./app-core/src/main/resources/org/koin/core/
}

function create_files {
    touch koin.properties
    echo "clientToken=clientToken" >> koin.properties
    echo "apiSessionV2=apiSessionV2" >> koin.properties
    echo "apiSessionV1=apiSessionV1" >> koin.properties
    echo "apiSession=apiSession" >> koin.properties
    echo "apiUrl=apiUrl" >> koin.properties
    echo "deviceType=deviceType" >> koin.properties
    echo "adUnitId=adUnitId" >> koin.properties
    echo "appId=appId" >> koin.properties
}

create_directories
create_files
