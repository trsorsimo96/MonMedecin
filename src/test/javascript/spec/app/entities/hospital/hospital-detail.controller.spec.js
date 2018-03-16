'use strict';

describe('Controller Tests', function() {

    describe('Hospital Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockHospital, MockSubCategory, MockQuarter, MockArrondissement;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockHospital = jasmine.createSpy('MockHospital');
            MockSubCategory = jasmine.createSpy('MockSubCategory');
            MockQuarter = jasmine.createSpy('MockQuarter');
            MockArrondissement = jasmine.createSpy('MockArrondissement');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Hospital': MockHospital,
                'SubCategory': MockSubCategory,
                'Quarter': MockQuarter,
                'Arrondissement': MockArrondissement
            };
            createController = function() {
                $injector.get('$controller')("HospitalDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'monMedecinApp:hospitalUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
