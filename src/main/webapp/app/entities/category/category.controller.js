(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('CategoryController', CategoryController);

    CategoryController.$inject = ['DataUtils', 'Category', 'CategorySearch'];

    function CategoryController(DataUtils, Category, CategorySearch) {

        var vm = this;

        vm.categories = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Category.query(function(result) {
                vm.categories = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CategorySearch.query({query: vm.searchQuery}, function(result) {
                vm.categories = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
