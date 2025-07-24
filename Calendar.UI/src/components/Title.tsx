import { CalendarIcon } from "@heroicons/react/16/solid"
import { Title as T } from '@muffled-ui/react'

export const Title: React.FC = () => (
  <T as="h1" display="flex" height="40px">
    <CalendarIcon width="40px" height="40px" />&nbsp; Calendar
  </T>
)