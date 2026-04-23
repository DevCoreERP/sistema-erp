import { Component } from '@angular/core';
import { Hero } from '../components/hero/hero';
import { Features } from '../components/features/features';
import { Benefits } from '../components/benefits/benefits';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [Navbar, Hero, Features, Benefits],
  templateUrl: './landing.html',
})
export class Landing {}