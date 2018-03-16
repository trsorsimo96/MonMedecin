(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('TownDetailController', TownDetailController);

    TownDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Town'];

    function TownDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Town) {
        var vm = this;

        vm.town = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('monMedecinApp:townUpdate', function(event, result) {
            vm.town = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
