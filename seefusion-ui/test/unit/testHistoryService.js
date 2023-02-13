describe('factory: HistoryService', function () {
	// Load your module.
	beforeEach(module('resources.history'));

	var service;

	beforeEach(inject(function(HistoryService){
		service = HistoryService;
	}));

	it('can get an instance of my factory', inject(function(HistoryService) {
		expect(service).toBeDefined();
	}));

	describe('getHistory',function(){
		it('should return an array of items',function(){
			expect(service.getHistory()).toBeDefined();

			var response;
			service.getHistory().then(function(a){
				response = a;
				expect(response.length).toEqual(0);
			});

			
		});
	});

});
