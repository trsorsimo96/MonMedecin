(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('ServiceController', ServiceController);

    ServiceController.$inject = ['DataUtils', 'Service', 'ServiceSearch'];

    function ServiceController(DataUtils, Service, ServiceSearch) {

        var vm = this;

        vm.services = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Service.query(function(result) {
                vm.services = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ServiceSearch.query({query: vm.searchQuery}, function(result) {
                vm.services = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
