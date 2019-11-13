#!/bin/bash

function create_domain_directories {
    local var target_dir='./app-domain/src/main/kotlin/co/anitrend/support/crunchyroll/domain'
    
    mkdir -p $target_dir/session/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/media/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/series/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/stream/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/collection/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/crunchyUser/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/locale/{entities,enums,interactors,models,repositories}
    mkdir -p $target_dir/news/{entities,enums,interactors,models,repositories}
}

create_domain_directories