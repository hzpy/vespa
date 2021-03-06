// Copyright 2019 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.

#include "entryref.hpp"

namespace search::datastore {

template EntryRefT<24u, 8u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<31u, 1u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<22u,10u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<19u,13u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<18u, 6u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<15u,17u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<10u,22u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT<10u,10u>::EntryRefT(uint64_t, uint32_t);
template EntryRefT< 3u, 2u>::EntryRefT(uint64_t, uint32_t);

}
