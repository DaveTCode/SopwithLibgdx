package net.tyler.messaging

trait MessagingComponent {
  def receive(x: Message)
}