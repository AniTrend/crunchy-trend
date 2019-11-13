#!/bin/bash

function create_respository_directories {
    local var target_dir='./app-data/src/main/kotlin/co/anitrend/support/crunchyroll/data'
    
    mkdir -p $target_dir/authentication/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/episode/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/session/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/media/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/series/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/stream/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/collection/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/user/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/locale/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/news/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
    mkdir -p $target_dir/tracker/{datasource/local/transformer,datasource/remote/,entity,koin,mapper,model,entity,repository,settings,usecase,helper,source/contract}
}

create_respository_directories
