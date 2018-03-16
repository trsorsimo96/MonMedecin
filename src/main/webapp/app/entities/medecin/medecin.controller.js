(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('MedecinController', MedecinController);

    MedecinController.$inject = ['Medecin', 'MedecinSearch'];

    function MedecinController(Medecin, MedecinSearch) {

        var vm = this;

        vm.medecins = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Medecin.query(function(result) {
                vm.medecins = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            MedecinSearch.query({query: vm.searchQuery}, function(result) {
                vm.medecins = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
