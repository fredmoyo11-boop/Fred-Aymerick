import {ResolveFn} from '@angular/router';
import {TripOffer, TripOfferService} from '../../api/sep_drive';
import {inject} from '@angular/core';

export const tripOfferResolver: ResolveFn<TripOffer> = (route, state) => {
  const id = +route.paramMap.get("id")!

  return inject(TripOfferService).getTripOffer(id);
};
