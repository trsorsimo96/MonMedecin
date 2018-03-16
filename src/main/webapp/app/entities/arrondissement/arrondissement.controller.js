(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('ArrondissementController', ArrondissementController);

    ArrondissementController.$inject = ['Arrondissement', 'ArrondissementSearch'];

    function ArrondissementController(Arrondissement, ArrondissementSearch) {

        var vm = this;

        vm.arrondissements = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Arrondissement.query(function(result) {
                vm.arrondissements = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ArrondissementSearch.query({query: vm.searchQuery}, function(result) {
                vm.arrondissements = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
