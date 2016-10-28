(function() {
    'use strict';
    angular
        .module('worktimetrackerApp')
        .factory('StandIn', StandIn);

    StandIn.$inject = ['$resource'];

    function StandIn ($resource) {
        var resourceUrl =  'api/stand-ins/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
