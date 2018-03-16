(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('QuarterController', QuarterController);

    QuarterController.$inject = ['Quarter', 'QuarterSearch'];

    function QuarterController(Quarter, QuarterSearch) {

        var vm = this;

        vm.quarters = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Quarter.query(function(result) {
                vm.quarters = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            QuarterSearch.query({query: vm.searchQuery}, function(result) {
                vm.quarters = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
