(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('HospitalController', HospitalController);

    HospitalController.$inject = ['DataUtils', 'Hospital', 'HospitalSearch'];

    function HospitalController(DataUtils, Hospital, HospitalSearch) {

        var vm = this;

        vm.hospitals = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Hospital.query(function(result) {
                vm.hospitals = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            HospitalSearch.query({query: vm.searchQuery}, function(result) {
                vm.hospitals = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
