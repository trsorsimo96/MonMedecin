(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('quarter', {
            parent: 'entity',
            url: '/quarter',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.quarter.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quarter/quarters.html',
                    controller: 'QuarterController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('quarter');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('quarter-detail', {
            parent: 'quarter',
            url: '/quarter/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.quarter.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/quarter/quarter-detail.html',
                    controller: 'QuarterDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('quarter');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Quarter', function($stateParams, Quarter) {
                    return Quarter.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'quarter',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('quarter-detail.edit', {
            parent: 'quarter-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quarter/quarter-dialog.html',
                    controller: 'QuarterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Quarter', function(Quarter) {
                            return Quarter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quarter.new', {
            parent: 'quarter',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quarter/quarter-dialog.html',
                    controller: 'QuarterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('quarter', null, { reload: 'quarter' });
                }, function() {
                    $state.go('quarter');
                });
            }]
        })
        .state('quarter.edit', {
            parent: 'quarter',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quarter/quarter-dialog.html',
                    controller: 'QuarterDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Quarter', function(Quarter) {
                            return Quarter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quarter', null, { reload: 'quarter' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('quarter.delete', {
            parent: 'quarter',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/quarter/quarter-delete-dialog.html',
                    controller: 'QuarterDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Quarter', function(Quarter) {
                            return Quarter.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('quarter', null, { reload: 'quarter' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
