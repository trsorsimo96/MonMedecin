(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('ArrondissementDetailController', ArrondissementDetailController);

    ArrondissementDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Arrondissement', 'Department'];

    function ArrondissementDetailController($scope, $rootScope, $stateParams, previousState, entity, Arrondissement, Department) {
        var vm = this;

        vm.arrondissement = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('monMedecinApp:arrondissementUpdate', function(event, result) {
            vm.arrondissement = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
