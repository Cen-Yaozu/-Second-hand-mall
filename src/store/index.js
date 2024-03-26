// src/store/index.js

import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
    isLoggedIn: false,
};

const mutations = {
    setIsLoggedIn(state, status) {
        state.isLoggedIn = status;
    },
};

export default new Vuex.Store({
    state,
    mutations,
});
