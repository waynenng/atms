import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-insert-card',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './insert-card.html',
  styleUrl: './insert-card.css'
})
export class InsertCardComponent {

  cardNumber: string = '';

  onInputChange() {
    this.cardNumber = this.cardNumber.replace(/\D/g, '');

    if (this.cardNumber.length > 16) {
      this.cardNumber = this.cardNumber.slice(0, 16);
    }
  }

  submit() {
    console.log(this.cardNumber);
  }
}
