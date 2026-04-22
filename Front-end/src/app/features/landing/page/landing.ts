import { Component } from '@angular/core';
import { Hero } from '../components/hero/hero';
import { Features } from '../components/features/features';
import { Benefits } from '../components/benefits/benefits';

@Component({
  selector: 'app-landing',
  imports: [Hero, Features, Benefits],
  templateUrl: './landing.html',
})
export class Landing {}
