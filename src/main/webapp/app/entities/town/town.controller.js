(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('TownController', TownController);

    TownController.$inject = ['DataUtils', 'Town', 'TownSearch'];

    function TownController(DataUtils, Town, TownSearch) {

        var vm = this;

        vm.towns = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Town.query(function(result) {
                vm.towns = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TownSearch.query({query: vm.searchQuery}, function(result) {
                vm.towns = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
