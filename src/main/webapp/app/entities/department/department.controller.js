(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('DepartmentController', DepartmentController);

    DepartmentController.$inject = ['Department', 'DepartmentSearch'];

    function DepartmentController(Department, DepartmentSearch) {

        var vm = this;

        vm.departments = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Department.query(function(result) {
                vm.departments = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            DepartmentSearch.query({query: vm.searchQuery}, function(result) {
                vm.departments = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
