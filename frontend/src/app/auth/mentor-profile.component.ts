import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';
import { Mentor } from './../entity/mentor';
import { EditMentorProfileModalComponent } from './edit-mentor-profile-modal/edit-mentor-profile-modal.component';

@Component({
  selector: 'app-mentor-profile',
  standalone: true,
  imports: [NgIf, EditMentorProfileModalComponent],
  templateUrl: './mentor-profile.component.html',
  styleUrls: ['./mentor-profile.component.css']
})
export class MentorProfileComponent {
  @Input() mentor!: Mentor;
  showEditModal = false;

  openEditModal() {
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
  }

  saveMentorProfile(updatedMentor: Mentor) {
    this.mentor = updatedMentor;
    this.closeEditModal();
  }
}
