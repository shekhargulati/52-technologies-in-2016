describe('controllers', () => {
  let vm;

  beforeEach(angular.mock.module('trello'));

  beforeEach(inject(($controller, webDevTec, toastr) => {
    spyOn(webDevTec, 'getTec').and.returnValue([{}, {}, {}, {}, {}]);
    spyOn(toastr, 'info').and.callThrough();

    vm = $controller('MainController');
  }));

  it('should have a timestamp creation date', () => {
    expect(vm.creationDate).toEqual(jasmine.any(Number));
  });

  it('should define animate class after delaying timeout', inject($timeout => {
    $timeout.flush();
    expect(vm.classAnimation).toEqual('rubberBand');
  }));

  it('should show a Toastr info and stop animation when invoke showToastr()', inject(toastr => {
    vm.showToastr();
    expect(toastr.info).toHaveBeenCalled();
    expect(vm.classAnimation).toEqual('');
  }));

  it('should define more than 5 awesome things', () => {
    expect(angular.isArray(vm.awesomeThings)).toBeTruthy();
    expect(vm.awesomeThings.length === 5).toBeTruthy();
  });
});
