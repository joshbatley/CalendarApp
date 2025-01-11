import { Box, DateRangePicker } from "@edgmont-ui/react"

type DatePickerProps = {
  onChange: any
}

export const DatePicker: React.FC<DatePickerProps> = ({ onChange }) => (
  <Box width="50%">
    <DateRangePicker onChange={onChange} inputValueFormat="do MMM yyyy" placeholder="Select Date" />
  </Box>
)