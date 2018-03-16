(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('RegionController', RegionController);

    RegionController.$inject = ['Region', 'RegionSearch'];

    function RegionController(Region, RegionSearch) {

        var vm = this;

        vm.regions = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Region.query(function(result) {
                vm.regions = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            RegionSearch.query({query: vm.searchQuery}, function(result) {
                vm.regions = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
